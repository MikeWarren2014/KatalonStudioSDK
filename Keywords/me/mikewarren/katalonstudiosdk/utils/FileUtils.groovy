package me.mikewarren.katalonstudiosdk.utils

public final class FileUtils {
	public static final String PathPartSeparatorsPattern = /[\/\\\\]/;
	public static final String CurrentDirectorySymbol = '.';

	public static String GetParentPathname(String filename) {
		String parentPath = new File(filename).getParent();

		if (parentPath == null)
			return null;

		StringBuilder builder = new StringBuilder();

		parentPath.split(this.PathPartSeparatorsPattern)
				.eachWithIndex { String token, int idx ->
					if (token.equals(''))
						return;
					if (idx == 0) {
						builder.append(token);
						return;
					}
					if (token.equals(this.CurrentDirectorySymbol))
						return;

					builder.append("${File.separator}${token}");
				}

		return builder.toString();
	}
}
