package android.app;

import android.content.Intent;

interface ILdkManager {
	boolean shouldSkipBroadcastIntent(in Intent intent, String targetPackageName);

	void addPackageWhiteList(String packageName);

	void addActionWhiteList(String packageName, String action);

	boolean isPackageInWhiteList(String packageName);

	List<String> getWhiteListActions(String packageName);
}
