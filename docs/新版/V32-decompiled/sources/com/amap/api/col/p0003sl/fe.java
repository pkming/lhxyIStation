package com.amap.api.col.p0003sl;

import android.app.Instrumentation;
import com.amap.api.services.auto.AutoTChargeStationResult;
import com.amap.api.services.auto.Classify;
import com.amap.api.services.auto.ListData;
import com.amap.api.services.auto.Lqii;
import com.amap.api.services.auto.Meta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: AutoTSearchResultParser.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fe {
    public static AutoTChargeStationResult a(JSONObject jSONObject) {
        AutoTChargeStationResult autoTChargeStationResult = new AutoTChargeStationResult();
        try {
            autoTChargeStationResult.code = jSONObject.optInt("code");
            autoTChargeStationResult.message = jSONObject.optString("message");
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
            if (jSONObjectOptJSONObject != null) {
                autoTChargeStationResult.classify = b(jSONObjectOptJSONObject.optJSONObject("classify"));
                autoTChargeStationResult.listData = h(jSONObjectOptJSONObject.optJSONObject("list_data"));
                autoTChargeStationResult.lqii = l(jSONObjectOptJSONObject.optJSONObject("lqii"));
                autoTChargeStationResult.meta = m(jSONObjectOptJSONObject.optJSONObject("meta"));
                autoTChargeStationResult.originJson = jSONObjectOptJSONObject.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return autoTChargeStationResult;
    }

    private static Classify b(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        Classify classify = new Classify();
        classify.itermData = c(jSONObject.optJSONObject("item_data"));
        classify.retainState = g(jSONObject.optJSONObject("retain_state"));
        return classify;
    }

    private static Classify.ItermData c(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        Classify.ItermData itermData = new Classify.ItermData();
        itermData.checkedNodes = a(jSONObject.optJSONArray("checked_nodes"));
        itermData.checkedValue = d(jSONObject.optJSONObject("checked_value"));
        itermData.datas = b(jSONObject.optJSONArray("data"));
        itermData.defaultPositionDatas = b(jSONObject.optJSONArray("default_position_data"));
        itermData.level2Datas = b(jSONObject.optJSONArray("level2_data"));
        return itermData;
    }

    private static List<Classify.CheckedNode> a(JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        if (jSONArray == null) {
            return arrayList;
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(0);
            if (jSONObjectOptJSONObject != null) {
                Classify.CheckedNode checkedNode = new Classify.CheckedNode();
                checkedNode.id = jSONObjectOptJSONObject.optString(Instrumentation.REPORT_KEY_IDENTIFIER);
                checkedNode.value = jSONObjectOptJSONObject.optString("value");
                checkedNode.name = jSONObjectOptJSONObject.optString("name");
                arrayList.add(checkedNode);
            }
        }
        return arrayList;
    }

    private static Classify.CheckedValue d(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        Classify.CheckedValue checkedValue = new Classify.CheckedValue();
        checkedValue.classifyV2Data = jSONObject.optString("classify_v2_data");
        checkedValue.classifyV2Level2Data = jSONObject.optString("classify_v2_level2_data");
        checkedValue.classifyV2Level3Data = jSONObject.optString("classify_v2_level3_data");
        return checkedValue;
    }

    private static List<Classify.Data> b(JSONArray jSONArray) {
        Classify.DataCategory dataCategoryE;
        ArrayList arrayList = new ArrayList();
        if (jSONArray == null) {
            return arrayList;
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
            if (jSONObjectOptJSONObject != null) {
                Classify.Data data = new Classify.Data();
                JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray("category");
                ArrayList arrayList2 = new ArrayList();
                if (jSONArrayOptJSONArray != null) {
                    for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                        JSONObject jSONObjectOptJSONObject2 = jSONArrayOptJSONArray.optJSONObject(i2);
                        if (jSONObjectOptJSONObject2 != null && (dataCategoryE = e(jSONObjectOptJSONObject2)) != null) {
                            arrayList2.add(dataCategoryE);
                        }
                    }
                }
                data.categories = arrayList2;
                data.checked = jSONObjectOptJSONObject.optInt("checked");
                data.classifyItemType = jSONObjectOptJSONObject.optString("classify_item_type");
                data.isCancelDefaultSelect = jSONObjectOptJSONObject.optInt("is_cancel_default_select");
                data.isNoBtn = jSONObjectOptJSONObject.optInt("is_no_btn");
                data.name = jSONObjectOptJSONObject.optString("name");
                data.params = jSONObjectOptJSONObject.optString("params");
                data.separator = jSONObjectOptJSONObject.optString("separator");
                data.type = jSONObjectOptJSONObject.optString("type");
                data.useCommonlyUsedConfig = jSONObjectOptJSONObject.optInt("use_commonly_used_config");
                data.useLocalConfig = jSONObjectOptJSONObject.optInt("use_local_config");
                data.useRemoteConfig = jSONObjectOptJSONObject.optInt("use_remote_config");
                data.multiSelect = jSONObjectOptJSONObject.optInt("multi_select");
                arrayList.add(data);
            }
        }
        return arrayList;
    }

    private static Classify.DataCategory e(JSONObject jSONObject) {
        Classify.Category categoryF;
        if (jSONObject == null) {
            return null;
        }
        Classify.DataCategory dataCategory = new Classify.DataCategory();
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("category");
        ArrayList arrayList = new ArrayList();
        if (jSONArrayOptJSONArray != null) {
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
                if (jSONObjectOptJSONObject != null && (categoryF = f(jSONObjectOptJSONObject)) != null) {
                    arrayList.add(categoryF);
                }
            }
        }
        dataCategory.categories = arrayList;
        dataCategory.checked = jSONObject.optInt("checked");
        dataCategory.defaultValue = jSONObject.optInt("default");
        dataCategory.componentType = jSONObject.optString("component_type");
        dataCategory.name = jSONObject.optString("name");
        dataCategory.params = jSONObject.optString("params");
        dataCategory.areaSubwayMark = jSONObject.optInt("area_subway_mark");
        dataCategory.hideTitle = jSONObject.optInt("hide_title");
        dataCategory.maxShowNum = jSONObject.optInt("max_show_num");
        dataCategory.maxShowNumRow = jSONObject.optInt("max_show_num_row");
        dataCategory.multiSelect = jSONObject.optInt("multi_select");
        dataCategory.img = jSONObject.optString("img");
        dataCategory.showType = jSONObject.optString("show_type");
        dataCategory.value = jSONObject.optString("value");
        dataCategory.classifyItemType = jSONObject.optString("classify_item_type");
        return dataCategory;
    }

    private static Classify.Category f(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        Classify.Category category = new Classify.Category();
        category.checked = jSONObject.optInt("checked");
        category.componentType = jSONObject.optString("component_type");
        category.defaultValue = jSONObject.optInt("default");
        category.name = jSONObject.optString("name");
        category.value = jSONObject.optString("value");
        category.classifyItemType = jSONObject.optString("classify_item_type");
        category.img = jSONObject.optString("img");
        category.showType = jSONObject.optString("show_type");
        category.params = jSONObject.optString("params");
        return category;
    }

    private static Classify.RetainState g(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        Classify.RetainState retainState = new Classify.RetainState();
        retainState.classifyBusinessType = jSONObject.optString("classify_business_type");
        retainState.classifyConf = jSONObject.optString("classify_conf");
        retainState.classifyRetainLevel2 = jSONObject.optString("classify_retain_level2");
        retainState.level2All = jSONObject.optString("level2_all");
        retainState.newClassifyCityadcode = jSONObject.optString("new_classify_cityadcode");
        retainState.newClassifyFlag = jSONObject.optString("new_classify_flag");
        return retainState;
    }

    private static ListData h(JSONObject jSONObject) {
        ListData.Content contentI;
        if (jSONObject == null) {
            return null;
        }
        ListData listData = new ListData();
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("content");
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
            if (jSONObjectOptJSONObject != null && (contentI = i(jSONObjectOptJSONObject)) != null) {
                arrayList.add(contentI);
            }
        }
        listData.content = arrayList;
        return listData;
    }

    private static ListData.Content i(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        ListData.Content content = new ListData.Content();
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
        if (jSONObjectOptJSONObject != null) {
            ListData.Data data = new ListData.Data();
            data.basicInfo = j(jSONObjectOptJSONObject.optJSONObject("basic_info"));
            data.chargingInfo = k(jSONObjectOptJSONObject.optJSONObject("charging_info"));
            content.data = data;
        }
        content.itemType = jSONObject.optString("item_type");
        return content;
    }

    private static ListData.BasicInfo j(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        ListData.BasicInfo basicInfo = new ListData.BasicInfo();
        basicInfo.adcode = jSONObject.optString("adcode");
        basicInfo.address = jSONObject.optString("address");
        basicInfo.averagecost = jSONObject.optString("averagecost");
        basicInfo.buildingStatus = jSONObject.optString("building_status");
        basicInfo.businessArea = jSONObject.optString("business_area");
        basicInfo.childShortname = jSONObject.optString("child_shortname");
        basicInfo.childShortnameEn = jSONObject.optString("child_shortname_en");
        basicInfo.childtype = jSONObject.optString("childtype");
        basicInfo.citycode = jSONObject.optString("citycode");
        basicInfo.cname = jSONObject.optString("cname");
        basicInfo.dispName = jSONObject.optString("disp_name");
        basicInfo.distance = jSONObject.optString("distance");
        basicInfo.dname = jSONObject.optString("dname");
        basicInfo.eaddress = jSONObject.optString("eaddress");
        basicInfo.endPoiExtension = jSONObject.optString("end_poi_extension");
        basicInfo.fNona = jSONObject.optString("f_nona");
        basicInfo.hisMark = jSONObject.optString("his_mark");
        basicInfo.hotText = jSONObject.optString("hot_text");
        basicInfo.id = jSONObject.optString(Instrumentation.REPORT_KEY_IDENTIFIER);
        basicInfo.industry = jSONObject.optString("industry");
        basicInfo.latitude = jSONObject.optString("latitude");
        basicInfo.longitude = jSONObject.optString("longitude");
        basicInfo.name = jSONObject.optString("name");
        basicInfo.category = jSONObject.optString("category");
        ListData.NaviVisited naviVisited = new ListData.NaviVisited();
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("navi_visited");
        if (jSONObjectOptJSONObject != null) {
            naviVisited.randUnionMonthUv = jSONObjectOptJSONObject.optString("rand_union_month_uv");
        }
        basicInfo.naviVisited = naviVisited;
        basicInfo.numSpaceW = jSONObject.optString("num_space_w");
        basicInfo.numSpaceWF = jSONObject.optString("num_space_w_f");
        basicInfo.opentime2 = jSONObject.optString("opentime2");
        basicInfo.opentimeText = jSONObject.optString("opentime_text");
        basicInfo.parent = jSONObject.optString("parent");
        basicInfo.parentName = jSONObject.optString("parent_name");
        basicInfo.picInfo = jSONObject.optString("pic_info");
        basicInfo.rating = jSONObject.optString("rating");
        basicInfo.recommendFlag = jSONObject.optString("recommend_flag");
        basicInfo.reviewTotal = jSONObject.optString("review_total");
        basicInfo.sell = jSONObject.optString("sell");
        ListData.ShortReview shortReview = new ListData.ShortReview();
        if (jSONObject.optJSONObject("short_review") != null) {
            basicInfo.shortReview = shortReview;
        }
        basicInfo.tel = jSONObject.optString("tel");
        basicInfo.towardsAngle = jSONObject.optString("towards_angle");
        basicInfo.typeCode = jSONObject.optString("typecode");
        basicInfo.updateFlag = jSONObject.optString("update_flag");
        basicInfo.xEntr = jSONObject.optString("x_entr");
        basicInfo.yEntr = jSONObject.optString("y_entr");
        return basicInfo;
    }

    private static ListData.ChargingInfo k(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        ListData.ChargingInfo chargingInfo = new ListData.ChargingInfo();
        chargingInfo.brandDesc = jSONObject.optString("brand_desc");
        chargingInfo.chargeInfo = c(jSONObject.optJSONArray("charge_info"));
        chargingInfo.chargingRatingFlagTerm = jSONObject.optString("charging_rating_flag_term");
        chargingInfo.creditZhima = jSONObject.optString("credit_zhima");
        chargingInfo.cscf = jSONObject.optString("cscf");
        chargingInfo.currentElePrice = jSONObject.optString("current_ele_price");
        chargingInfo.currentSerPrice = jSONObject.optString("current_ser_price");
        chargingInfo.deepRightsTag = jSONObject.optString("deep_rights_tag");
        chargingInfo.latestCharge = jSONObject.optString("latest_charge");
        chargingInfo.numFast = jSONObject.optString("num_fast");
        chargingInfo.numSlow = jSONObject.optString("num_slow");
        chargingInfo.parkCategory = jSONObject.optString("park_category");
        chargingInfo.priceChargingPark = d(jSONObject.optJSONArray("price_charging_pack"));
        chargingInfo.priceParkingStd = jSONObject.optString("price_parking_std");
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("iddictionary");
        if (jSONObjectOptJSONObject != null) {
            HashMap<String, String> map = new HashMap<>();
            Iterator<String> itKeys = jSONObjectOptJSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                map.put(next, jSONObjectOptJSONObject.optString(next));
            }
            chargingInfo.idDictionary = map;
        }
        return chargingInfo;
    }

    private static List<ListData.ChargeInfo> c(JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        if (jSONArray == null) {
            return arrayList;
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
            if (jSONObjectOptJSONObject != null) {
                ListData.ChargeInfo chargeInfo = new ListData.ChargeInfo();
                JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray("plugs_info");
                if (jSONArrayOptJSONArray != null) {
                    ArrayList arrayList2 = new ArrayList();
                    for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                        JSONObject jSONObjectOptJSONObject2 = jSONArrayOptJSONArray.optJSONObject(i2);
                        if (jSONObjectOptJSONObject2 != null) {
                            ListData.PlugsInfo plugsInfo = new ListData.PlugsInfo();
                            plugsInfo.brandDesc = jSONObjectOptJSONObject2.optString("brand_desc");
                            plugsInfo.fastcur = jSONObjectOptJSONObject2.optString("fastcur");
                            plugsInfo.fastpower = jSONObjectOptJSONObject2.optString("fastpower");
                            plugsInfo.fastvol = jSONObjectOptJSONObject2.optString("fastvol");
                            arrayList2.add(plugsInfo);
                        }
                    }
                    chargeInfo.plugsInfos = arrayList2;
                    chargeInfo.plugsType = jSONObjectOptJSONObject.optString("plugstype");
                    arrayList.add(chargeInfo);
                }
            }
        }
        return arrayList;
    }

    private static ListData.PriceChargingPark d(JSONArray jSONArray) {
        if (jSONArray == null) {
            return null;
        }
        ListData.PriceChargingPark priceChargingPark = new ListData.PriceChargingPark();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
            if (jSONObjectOptJSONObject != null) {
                ListData.Park park = new ListData.Park();
                park.srcType = jSONObjectOptJSONObject.optString("src_type");
                JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray("price_charging");
                if (jSONArrayOptJSONArray != null) {
                    ArrayList arrayList2 = new ArrayList();
                    for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                        JSONObject jSONObjectOptJSONObject2 = jSONArrayOptJSONArray.optJSONObject(i2);
                        if (jSONObjectOptJSONObject2 != null) {
                            ListData.PriceCharging priceCharging = new ListData.PriceCharging();
                            priceCharging.elePrice = jSONObjectOptJSONObject2.optString("ele_price");
                            priceCharging.serPrice = jSONObjectOptJSONObject2.optString("ser_price");
                            priceCharging.time = jSONObjectOptJSONObject2.optString("time");
                            priceCharging.updatetime = jSONObjectOptJSONObject2.optInt("updatetime");
                            arrayList2.add(priceCharging);
                        }
                    }
                    park.priceChargings = arrayList2;
                }
                arrayList.add(park);
            }
        }
        priceChargingPark.parks = arrayList;
        return priceChargingPark;
    }

    private static Lqii l(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        Lqii lqii = new Lqii();
        lqii.categoryBrandRecognitionResult = jSONObject.optString("category_brand_recognition_result");
        lqii.changeQueryTip = jSONObject.optString("change_query_tip");
        lqii.expandRangeTip = jSONObject.optString("change_query_tip");
        lqii.isCurrentCity = jSONObject.optString("is_current_city");
        lqii.isUserCity = jSONObject.optString("is_user_city");
        lqii.queryCateResult = jSONObject.optString("query_cate_result");
        lqii.suggestContent = jSONObject.optString("suggestcontent");
        lqii.targetViewCity = jSONObject.optString("target_view_city");
        lqii.totalhits = jSONObject.optString("totalhits");
        lqii.viewRegion = jSONObject.optString("view_region");
        return lqii;
    }

    private static Meta m(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        Meta meta = new Meta();
        meta.listBizType = jSONObject.optString("list_biz_type");
        return meta;
    }
}
