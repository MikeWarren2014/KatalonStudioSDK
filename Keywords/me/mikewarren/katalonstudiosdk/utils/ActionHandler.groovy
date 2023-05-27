package me.mikewarren.katalonstudiosdk.utils

public class ActionHandler {
	public static Object HandleReturnableAction(Closure onAction, Closure<Object> onDone, long timeOut) {
		long startTime = System.currentTimeSeconds();
		while (System.currentTimeSeconds() < startTime + timeOut) {
			try {
				final Object result = onDone(true, onAction());
				if (result)
					return result;
			} catch (Exception ex) {
				onDone(false, ex);
			}
		}

		return null;
	}

	public static void HandleFailableAction(Closure onAction, Closure<Object> onDone, long timeOut) {
		long startTime = System.currentTimeSeconds();
		while (System.currentTimeSeconds() < startTime + timeOut) {
			try {
				onAction();
				onDone(true, null);
				return;
			} catch (Exception ex) {
				onDone(false, ex);
			}
		}
	}
}
