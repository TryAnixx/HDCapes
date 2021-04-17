package de.tryanixx.hdcapes.authenticate;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Authenticator {
    public static final String SERVER_HASH = "5b65c3ce12db8a41cb2a69be14d51b30b75698d8";

    public boolean authenticate(String serverHash) {
        Minecraft mc = Minecraft.getMinecraft();
        Session session = mc.getSession();
        if (session == null) {
            return false;
        }
        try {
            mc.getSessionService().joinServer(session.getProfile(), session.getToken(), serverHash);
            return true;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return false;
        }
    }
}
