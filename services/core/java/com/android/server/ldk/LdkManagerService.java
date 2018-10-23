/*
 * Copyright (C)  2018 ldkxingzhe@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0;
 */
package com.android.server.ldk;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Intent;
import android.content.Context;
import android.util.Slog;
import android.util.ArraySet;
import android.util.ArrayMap;
import android.app.ILdkManager;

import android.os.ServiceManager;

import java.util.List;
import java.util.ArrayList;

/**
 * A customized server write by me.  it providers these functions:
 * 
 * - check a broadcast to a dead process should skip
 */
public class LdkManagerService extends ILdkManager.Stub {
	private static final String TAG = "LdkManager";
	private static final boolean DEBUG = true;
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final ArraySet<String> whiteListPackageName = new ArraySet<>();
	private final ArrayMap<String, ArraySet<String>> whiteListActions = new ArrayMap<>();

	@Override
	public boolean shouldSkipBroadcastIntent(Intent intent, String targetPackageName){
		String action = intent.getAction();
		if(action == null)
			return false;
		Lock readLock = readWriteLock.readLock();
		try{
			readLock.lock();
			if(whiteListPackageName.contains(targetPackageName)){
				// in whitelist, and allow all send all braodcast
				return false;
			}
			return true;
		}finally{
			readLock.unlock();
		}
	}

	@Override
	public void addPackageWhiteList(String packageName){
		Lock writeLock = readWriteLock.writeLock();
		try{
			writeLock.lock();
			whiteListPackageName.add(packageName);
		}finally{
			writeLock.unlock();
		}
	}

	@Override
	public void addActionWhiteList(String packageName, String action){
		Lock writeLock = readWriteLock.writeLock();
		try{
			writeLock.lock();
			ArraySet<String> actions = whiteListActions.get(packageName);
			if(actions == null){
				actions = new ArraySet<>();
				whiteListActions.put(packageName, actions);
			}
			actions.add(action);
		}finally{
			writeLock.unlock();
		}
	}

	@Override
	public boolean isPackageInWhiteList(String packageName){
		Lock readLock = readWriteLock.readLock();
		try{
			readLock.lock();
			return whiteListPackageName.contains(packageName);
		}finally{
			readLock.unlock();
		}
	}

	@Override
	public List<String> getWhiteListActions(String packageName){
		Lock readLock = readWriteLock.readLock();
		try{
			readLock.lock();
			return new ArrayList(whiteListActions.get(packageName));
		}finally{
			readLock.unlock();
		}
	}


	public static LdkManagerService main(Context context){
		if(DEBUG){
			Slog.v(TAG, "first init LdkManagerService.");
		}
		LdkManagerService ldkService = new LdkManagerService();
		ServiceManager.addService(Context.LDK_MANAGER_SERVICE, ldkService);
		return ldkService;
	}
}
