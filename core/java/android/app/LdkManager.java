package android.app;

import android.annotation.SystemService;
import android.os.RemoteException;
import android.content.Context;

import java.util.List;

@SystemService(Context.LDK_MANAGER_SERVICE)
public class LdkManager{

	private final ILdkManager mService;
	
	public LdkManager(ILdkManager service){
		mService = service;
	}

	boolean isPackageInWhiteList(String packageName){
		try{
			return mService.isPackageInWhiteList(packageName);
		}catch(RemoteException e){
			throw new RuntimeException(e);
		}
	}

	List<String> getWhiteListActions(String packageName){
		try{
			return mService.getWhiteListActions(packageName);
		}catch(RemoteException e){
			throw new RuntimeException(e);
		}
	}

	void addPackageWhiteList(String packageName){
		try{
			mService.addPackageWhiteList(packageName);
		}catch(RemoteException e){
			throw new RuntimeException(e);
		}
	}

	void addActionWhiteList(String packageName, String action){
		try{
			mService.addActionWhiteList(packageName, action);
		}catch(RemoteException e){
			throw new RuntimeException(e);
		}
	}
}
