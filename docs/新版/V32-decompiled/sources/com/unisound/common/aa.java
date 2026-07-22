package com.unisound.common;

import android.app.backup.FullBackup;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: classes2.dex */
public class aa {
    private static boolean a = true;
    private static final String b = "ResultToJsonUtil";

    public static JSONObject a(InputStream inputStream, float f) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        JSONArray jSONArray2 = new JSONArray();
        NodeList childNodes = ((Element) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).getChildNodes().item(0)).getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == 1) {
                a(new JSONObject(), jSONArray, new JSONArray(), 0.0f, childNodes.item(i).getNodeName(), childNodes.item(i).getFirstChild().getNodeValue());
            }
        }
        jSONObject.put("l", jSONArray);
        jSONObject.put("score", f);
        jSONArray2.put(jSONObject);
        return new JSONObject().put(FullBackup.CACHE_TREE_TOKEN, jSONArray2);
    }

    public static JSONObject a(String str, float f, String str2) throws JSONException {
        a(str2, str);
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        JSONArray jSONArray2 = new JSONArray();
        NodeList childNodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(str2)).getChildNodes();
        if (a) {
            r.c(b, "FileUtiles -Node.length =" + childNodes.getLength() + ", node:value =" + childNodes.item(0).getNodeName());
        }
        Element element = (Element) childNodes.item(0);
        NodeList childNodes2 = element.getChildNodes();
        if (a) {
            r.c(b, "FileUtiles -childElement =" + element.getNodeValue());
        }
        for (int i = 0; i < childNodes2.getLength(); i++) {
            if (childNodes2.item(i).getNodeType() == 1) {
                a(new JSONObject(), jSONArray, new JSONArray(), 0.0f, childNodes2.item(i).getNodeName().replace("_", ""), childNodes2.item(i).getFirstChild().getNodeValue());
            }
        }
        jSONObject.put("l", jSONArray);
        jSONObject.put("score", f);
        jSONArray2.put(jSONObject);
        return new JSONObject().put(FullBackup.CACHE_TREE_TOKEN, jSONArray2);
    }

    public static void a(String str, String str2) {
        try {
            String str3 = "<?xml version='1.0' encoding='utf-8'?>" + str2.replace("[", "").replace("]", "").trim();
            if (a) {
                r.c(b, "xmlText = " + str3);
            }
            a(new File(str));
            FileWriter fileWriter = new FileWriter(str, true);
            fileWriter.write(str3);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void a(JSONObject jSONObject, JSONArray jSONArray, JSONArray jSONArray2, float f, String str, String str2) {
        try {
            jSONObject.put("s", f);
            jSONObject.put("t", str);
            jSONObject.put("w", str2);
            jSONArray2.put(jSONObject);
            jSONArray.put(new JSONObject().put(FullBackup.CACHE_TREE_TOKEN, jSONArray2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static boolean a(File file) {
        if (!file.exists()) {
            b(file.getParentFile());
        }
        file.delete();
        return file.createNewFile();
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0107  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static org.json.JSONObject b(java.lang.String r12, float r13, java.lang.String r14) throws org.xmlpull.v1.XmlPullParserException, org.json.JSONException {
        /*
            Method dump skipped, instruction units count: 386
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.common.aa.b(java.lang.String, float, java.lang.String):org.json.JSONObject");
    }

    private static void b(File file) {
        if (!file.getParentFile().exists()) {
            b(file.getParentFile());
        }
        file.mkdir();
    }
}
