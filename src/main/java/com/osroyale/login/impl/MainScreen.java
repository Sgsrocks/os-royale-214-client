package com.osroyale.login.impl;


import com.osroyale.*;
import com.osroyale.login.LoginComponent;
import com.osroyale.login.ScreenType;

/**
 * Handles the main screen of login.
 *
 * @author Daniel
 */
public class MainScreen extends LoginComponent {

	private static final int EMAIL_CHARACTER_LIMIT = 28;

	@Override
	public void render(Client client) {
		//System.out.println("Rendering");
		int centerX = getX();
		int centerY = getY();
		refresh(client);
		load(client, 10);

        /* Message Check */
		if (client.loginMessage1.length() > 0 || client.loginMessage2.length() > 0) {
			client.loginRenderer.setScreen(new MessageScreen());
		}

        /* Background */
		Client.spriteCache.get(57).drawTransparentSprite((Client.frameWidth / 2) - (Client.spriteCache.get(57).width / 2), (Client.frameHeight / 2) - (Client.spriteCache.get(57).height / 2), client.loginTick);

        /* Login Button */
		if (client.mouseInRegion(centerX - 33, centerY + 66, centerX + 208, centerY + 107)) {
			Client.spriteCache.get(59).drawSprite(350, 320, 46);
			addTooltip("Log into RunePK");
		} else {
			Client.spriteCache.get(58).drawTransparentSprite(350, 320, client.loginTick);
		}

        /* Remember Me Button */
		Client.spriteCache.get(Settings.REMEMBER_ME ? 180 : 178).drawTransparentSprite(430, 287, client.loginTick);
		if (client.mouseInRegion(centerX - 27, centerY + 27, centerX + 61, centerY + 46)) {
			addTooltip("Toggle remember account details");
		}

        /* Information */
		if (client.mouseInRegion(centerX - 26, centerY - 81, centerX + 203, centerY - 49)) {
			Client.spriteCache.get(677).drawTransparentSprite(356, 172, 100);
			addTooltip("Enter your username");
		}
		if (client.mouseInRegion(centerX - 26, centerY - 6, centerX + 203, centerY + 23)) {
			Client.spriteCache.get(677).drawTransparentSprite(356, 245, 100);
			addTooltip("Enter your password");
		}

		client.regularText.drawText(true, centerX - 20, 0xFFFFFF, Utility.formatName(client.myUsername) + ((client.loginScreenCursorPos == 0) & (Client.tick % 40 < 20) ? "|" : ""), centerY - 50);
		client.regularText.drawText(true, centerX - 20, 0xFFFFFF, StringUtils.toAsterisks(client.myPassword) + ((client.loginScreenCursorPos == 1) & (Client.tick % 40 < 20) ? "|" : ""), centerY + 23);

        /* World Button */
		if (client.mouseInRegion(centerX - 58, centerY + 132, centerX + 27, centerY + 158)) {
			Client.spriteCache.get(61).drawSprite(324, 386, 36);
			addTooltip("Toggle world");
		} else {
			Client.spriteCache.get(60).drawTransparentSprite(324, 386, 50);
		}

		client.regularText.drawCenteredText(0xFFFFFF, 360, "W" + (Configuration.CONNECTION.ordinal() + 1) + ":", 406, false);
		client.regularText.drawCenteredText(0x4FB533, 385, Configuration.CONNECTION.name, 406, false);

        /* Social Buttons */
		if (client.mouseInRegion(centerX + 207, centerY + 136, centerX + 231, centerY + 157)) {
			Client.spriteCache.get(191).drawTransparentSprite(590, 387, 50);
			addTooltip("View youtube page");
		} else {
			Client.spriteCache.get(190).drawTransparentSprite(590, 387, 255);
		}
		if (client.mouseInRegion(centerX + 177, centerY + 136, centerX + 201, centerY + 157)) {
			Client.spriteCache.get(189).drawTransparentSprite(560, 387, 50);
			addTooltip("View twitter page");
		} else {
			Client.spriteCache.get(188).drawTransparentSprite(560, 387, 255);
		}
		if (client.mouseInRegion(centerX + 147, centerY + 136, centerX + 171, centerY + 157)) {
			Client.spriteCache.get(187).drawTransparentSprite(530, 387, 50);
			addTooltip("Join discord channel");
		} else {
			Client.spriteCache.get(186).drawTransparentSprite(530, 387, 255);
		}

        /* Forgot Password */
		if (client.mouseInRegion(centerX - 33, centerY + 110, centerX + 82, centerY + 118)) {
			client.smallFont.drawCenteredText(0x22747D, 407, "Forgot your password?", 375, false);
			addTooltip("Click to recover your password");
		} else {
			client.smallFont.drawCenteredText(0x7B7B7B, 407, "Forgot your password?", 375, false);
		}

        /* Announcement */
		announcement(client);

        /* Account */
		drawAccount(client);

        /* Bubble */
		drawSetting(client);

        /* Other */
		client.smallFont.drawCenteredText(0x27A2B0, centerX + 330, "Client Build: " + Configuration.GAME_VERSION, centerY + 245, true);
		//        if (Configuration.DEBUG_MODE) {
		//            client.smallFont.drawCenteredText(0xFFFFFF, centerX + 300, "MouseX: " + (client.mouseX - (centerX)) + " Mouse Y: " + (client.mouseY - (centerY)), centerY - 225, true);
		//        }

        /* Drawing */
		Client.loginScreenIP.drawGraphics(client.graphics, 0, 0);
		Raster.reset();
	}

	@Override
	public void click(Client client) {
		int centerX = getX();
		int centerY = getY();

        /* Username */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 26, centerY - 81, centerX + 203, centerY - 49))
			client.loginScreenCursorPos = 0;

        /* Password */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 26, centerY - 6, centerX + 203, centerY + 23))
			client.loginScreenCursorPos = 1;

        /* Forgot Password */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 33, centerY + 110, centerX + 82, centerY + 118))
			Utility.launchURL("https://www.runepk.io/community/index.php?/lostpassword/");

        /* Remember Me */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 27, centerY + 27, centerX + 61, centerY + 46)) {
			Settings.REMEMBER_ME = !Settings.REMEMBER_ME;
		}

        /* Social Buttons */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX + 147, centerY + 136, centerX + 171, centerY + 157)) {
			Utility.launchURL("https://discord.me/runepk");
		}
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX + 177, centerY + 136, centerX + 201, centerY + 157)) {
			Utility.launchURL("https://twitter.com/RunePK");
		}
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX + 207, centerY + 136, centerX + 231, centerY + 157)) {
			Utility.launchURL("https://www.youtube.com/channel/UC8wR4kTWHgeD1F_1XTtA-wA");
		}

        /* Account */
		int yPos = centerY - 70;
		if (AccountManager.ACCOUNTS != null) {
			for (int index = 0; index < AccountManager.ACCOUNTS.size(); index++, yPos += 105) {
				AccountData accountData = AccountManager.ACCOUNTS.get(index);
				if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 230, yPos - 84, centerX - 63, yPos - 5)) {
					client.lastAccount = accountData;
					client.myUsername = Utility.formatName(accountData.username.toLowerCase());
					client.myPassword = accountData.password;
					client.attemptLogin(accountData.username, accountData.password, false);
					if (Client.loggedIn) {
						return;
					}
				}
				if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 230, yPos - 2, centerX - 83, yPos + Client.spriteCache.get(676).height)) {
					if (client.lastAccount == null) {
						client.loginMessage1 = "There was an issue loading your account.";
						return;
					}
					client.lastAccount = accountData;
					client.loginRenderer.setScreen(new AccountScreen());
					return;
				}
				if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 80, yPos, centerX - Client.spriteCache.get(675).width - 45, yPos + 20)) {
					AccountManager.removeAccount(accountData);
				}
			}
		}

        /* Bubble */
		settingButton(client);

        /* World Button */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 58, centerY + 132, centerX + 27, centerY + 158)) {
			switch (Configuration.CONNECTION) {
				case ECONOMY:
					Configuration.CONNECTION = Connection.MANAGEMENT;
					break;
				case MANAGEMENT:
					Configuration.CONNECTION = Connection.DEVELOPMENT;
					break;
				case DEVELOPMENT:
					Configuration.CONNECTION = Connection.ECONOMY;
					break;
			}
			Client.server = Configuration.CONNECTION.address;
		}

        /* Login Buttons */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 33, centerY + 66, centerX + 208, centerY + 107))
		{
			if (!Client.loggedIn)
			{
				client.attemptLogin(client.myUsername, client.myPassword, false);
			}
		}

        /* Writing */
		handleWriting(client);
	}

	/** Handles drawing the accounts on the login screen.  */
	@SuppressWarnings("ConstantConditions")
	private void drawAccount(Client client) {
		Raster.fillRectangle(155, 100, 164, 313, 0x2B3738, 150);

		int centerX = getX();
		int centerY = getY();
		int yPos = centerY - 70;
		for (int index = 0; index < 3; index++, yPos += 105) {
			AccountData accountData = AccountManager.get(index);

			if (accountData == null) {
//				Client.spriteCache.get(73).drawARGBSprite(centerX - 176, yPos - 70);
//				Client.spriteCache.get(676).drawARGBSprite(centerX - 223, yPos - 2);
			} else {
				if (client.mouseInRegion(centerX - 80, yPos, centerX - Client.spriteCache.get(675).width - 45, yPos + 20)) {
					Client.spriteCache.get(675).drawARGBSprite(centerX - 80, yPos);
					addTooltip("Delete profile");
				} else {
					Client.spriteCache.get(674).drawARGBSprite(centerX - 80, yPos);
				}
				Client.spriteCache.get(accountData.avatar).drawARGBSprite(centerX - 182, yPos - 80);
				Client.spriteCache.get(676).drawARGBSprite(centerX - 223, yPos - 2);
				int rank = (accountData.rank - 1);
				String name = accountData.username;
				if (rank <= -1) {
					client.newSmallFont.drawCenteredString(Utility.formatName(name), centerX - 145, yPos + 16);
				} else {
					client.newSmallFont.drawCenteredString("<img=" + rank + ">", centerX - 145, yPos + 13);
					client.newSmallFont.drawCenteredString("<col=ffffff>" + Utility.formatName(name.toLowerCase()), centerX - 145, yPos + 22);
				}
				if (client.mouseInRegion(centerX - 227, yPos - 2, centerX - 83, yPos + Client.spriteCache.get(676).height)) {
					Raster.drawRectangle(centerX - 227, yPos - 2, Client.spriteCache.get(676).width, Client.spriteCache.get(676).height, 0x329EAB);
					Raster.fillRectangle(centerX - 227, yPos - 2, Client.spriteCache.get(676).width, Client.spriteCache.get(676).height, 0x539299, 50);
					addTooltip("Manage profile details");
				}
				if (client.mouseInRegion(centerX - 230, yPos - 84, centerX - 63, yPos - 5)) {
					Raster.drawRectangle(155, yPos - 81, 164, 80, 0x329EAB);
					Raster.fillRectangle(155, yPos - 81, 164, 80, 0x539299, 50);
					addTooltip("Log into game with profile");
				}
			}
		}
	}

	/**
	 * Handles writing in the client.
	 */
	private void handleWriting(Client client) {
		do {
			int line = client.readChar(-796);
			if (line == -1)
				break;
			boolean flag = false;
			for (int index = 0; index < Client.validUserPassChars.length(); index++) {
				if (line != Client.validUserPassChars.charAt(index))
					continue;
				flag = true;
				break;
			}

			// Main account username
			if (client.loginScreenCursorPos == 0) {
				if (line == 8 && client.myUsername.length() > 0)
					client.myUsername = client.myUsername.substring(0, client.myUsername.length() - 1);
				if (line == 9 || line == 10 || line == 13) {
					client.loginScreenCursorPos = 1;
				}
				if (flag) {
					client.myUsername += (char) line;
				}

				if (client.myUsername.length() > EMAIL_CHARACTER_LIMIT) {
					client.myUsername = client.myUsername.substring(0, EMAIL_CHARACTER_LIMIT);
				}

				// Main account password
			} else if (client.loginScreenCursorPos == 1) {
				if (line == 8 && client.myPassword.length() > 0)
					client.myPassword = client.myPassword.substring(0, client.myPassword.length() - 1);
				if (line == 9 || line == 10 || line == 13) {
					client.attemptLogin(client.myUsername, client.myPassword, false);
				}
				if (flag) {
					client.myPassword += (char) line;
				}
				if (client.myPassword.length() > 20) {
					client.myPassword = client.myPassword.substring(0, 20);
				}
			}
		} while (true);
		return;
	}

	@Override
	public ScreenType type() {
		return ScreenType.MAIN;
	}
}
