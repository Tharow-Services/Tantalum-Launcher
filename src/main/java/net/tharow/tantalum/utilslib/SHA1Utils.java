/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2018 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.utilslib;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;


public class SHA1Utils {

    public static String getSHA1(File file) {
        try {
            InputStream filestream = new FileInputStream(file);
            return DigestUtils.sha1Hex(filestream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkSHA1(File file, String sha1) {
        return checkSHA1(sha1, getSHA1(file));
    }

    public static boolean checkSHA1(String sha1, String otherSha1) {
        return sha1.equalsIgnoreCase(otherSha1);
    }
}
