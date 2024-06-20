package com.customapi.fmkpl.service;

import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;
import com.customapi.fmkpl.BuildConfig;
import com.customapi.fmkpl.bean.WorkConfig;
import com.customapi.fmkpl.datas.WorkId;
import com.customapi.fmkpl.tool.MyTool;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccManager {
    private WorkId mWorkId = new WorkId();
    private WorkConfig mWorkConfig;
    private Gson mGson;
    private List<String> orderTypes;
    private List<String> fromWordsone;
    private List<String> toWordsone;

    private List<String> fromWordstwo;
    private List<String> toWordstwo;
    private List<String> fromWordsthree;
    private List<String> toWordsthree;
    private List<String> fromWordsfour;
    private List<String> toWordsfour;
    private List<String> fromWordsfive;
    private List<String> toWordsfive;
    private LogInterface mLogInterface;
    public AccManager(LogInterface logInterface){
        mLogInterface = logInterface;

    }
    public void updateConfig(WorkConfig workConfig){
        mWorkConfig = workConfig;
        if (mWorkConfig.getOrder_types().isEmpty()) {
            orderTypes = new ArrayList<>();
        } else {
            orderTypes = Arrays.asList(mWorkConfig.getOrder_types().split("#"));
        }
        if (mWorkConfig.getFrom_place_one().isEmpty()) {
            fromWordsone = new ArrayList<>();
        } else {
            fromWordsone = Arrays.asList(mWorkConfig.getFrom_place_one().split("#"));
        }
        if (mWorkConfig.getTo_place_one().isEmpty()) {
            toWordsone = new ArrayList<>();
        } else {
            toWordsone = Arrays.asList(mWorkConfig.getTo_place_one().split("#"));
        }
        if (mWorkConfig.getFrom_place_two().isEmpty()) {
            fromWordstwo = new ArrayList<>();
        } else {
            fromWordstwo = Arrays.asList(mWorkConfig.getFrom_place_two().split("#"));
        }
        if (mWorkConfig.getTo_place_two().isEmpty()) {
            toWordstwo = new ArrayList<>();
        } else {
            toWordstwo = Arrays.asList(mWorkConfig.getTo_place_two().split("#"));
        }
        if (mWorkConfig.getFrom_place_three().isEmpty()) {
            fromWordsthree = new ArrayList<>();
        } else {
            fromWordsthree = Arrays.asList(mWorkConfig.getFrom_place_three().split("#"));
        }
        if (mWorkConfig.getTo_place_three().isEmpty()) {
            toWordsthree = new ArrayList<>();
        } else {
            toWordsthree = Arrays.asList(mWorkConfig.getTo_place_three().split("#"));
        }

        if (mWorkConfig.getFrom_place_four().isEmpty()) {
            fromWordsfour = new ArrayList<>();
        } else {
            fromWordsfour = Arrays.asList(mWorkConfig.getFrom_place_four().split("#"));
        }
        if (mWorkConfig.getTo_place_four().isEmpty()) {
            toWordsfour = new ArrayList<>();
        } else {
            toWordsfour = Arrays.asList(mWorkConfig.getTo_place_four().split("#"));
        }

        if (mWorkConfig.getFrom_place_five().isEmpty()) {
            fromWordsfive = new ArrayList<>();
        } else {
            fromWordsfive = Arrays.asList(mWorkConfig.getFrom_place_five().split("#"));
        }
        if (mWorkConfig.getTo_place_five().isEmpty()) {
            toWordsfive = new ArrayList<>();
        } else {
            toWordsfive = Arrays.asList(mWorkConfig.getTo_place_five().split("#"));
        }
    }
    public void find_order_one(AccessibilityNodeInfo rootNode){
        try{
            if(mWorkConfig==null){
                return;
            }
            AccessibilityNodeInfo order_node = findFirstVisibleNodeById(rootNode,mWorkId.getRl_short_distance_order());
            if(order_node!=null){
                AccessibilityNodeInfo type_node = findFirstNodeById(order_node,mWorkId.getTv_source_desc());

                if(!ordertype_isok(type_node)){
                    mLogInterface.logShow("类型不符合");
                    AccessibilityNodeInfo cancel_node = findFirstNodeById(order_node,mWorkId.getBtn_short_distance_cancel());
                    nativecoor_click(cancel_node);
                    return;
                }
                if(checkorder(order_node)){
                    AccessibilityNodeInfo confirm_node = findFirstNodeById(order_node,mWorkId.getBtn_short_distance_grab_order());
//                        实际抢
                    nativecoor_click(confirm_node);
                    AccessibilityNodeInfo money_node = findFirstNodeById(order_node,mWorkId.getTv_expect_money());
                    mLogInterface.logShow("订单金额："+MyTool.char2string(money_node.getText())+"元 符合要求");
                }else{
                    AccessibilityNodeInfo cancel_node = findFirstNodeById(order_node,mWorkId.getBtn_short_distance_cancel());
                    nativecoor_click(cancel_node);

                }
            }
        }catch (Exception e){
            mLogInterface.logShow("jvm err："+e.toString());
        }

    }

    private boolean checkorder(AccessibilityNodeInfo order_node){
        AccessibilityNodeInfo distance_node = findFirstNodeById(order_node,mWorkId.getTv_expect_arrive_distance());
        String distance_str = MyTool.char2string(distance_node.getText());
        AccessibilityNodeInfo licheng_node = findFirstNodeById(order_node,mWorkId.getTv_expect_distance());
        String licheng_str = MyTool.char2string(licheng_node.getText());

        mLogInterface.logShow("订单：距离:"+distance_str);
        mLogInterface.logShow("订单：里程:"+licheng_str);
        float distance = MyTool.getFloatValue(distance_str);
        if(distance_str.contains("公里")){
            distance = distance*1000;
        }

        float licheng = MyTool.getFloatValue(licheng_str);
        if(licheng_str.contains("公里")){
            licheng = licheng*1000;
        }

        if(mWorkConfig.isType_one_state()){
            if(check_type_one(order_node,distance,licheng)){
                return true;
            }else{
                mLogInterface.logShow("一档不符合");
            }
        }
        if(mWorkConfig.isType_two_state()){
            if(check_type_two(order_node,distance,licheng)){
                return true;
            }else{
                mLogInterface.logShow("二档不符合");
            }
        }
        if(mWorkConfig.isType_three_state()){
            if(check_type_three(order_node,distance,licheng)){
                return true;
            }else{
                mLogInterface.logShow("三档不符合");
            }
        }
        if(mWorkConfig.isType_four_state()){
            if(check_type_four(order_node,distance,licheng)){
                return true;
            }else{
                mLogInterface.logShow("四档不符合");
            }
        }
        return false;
    }
    private Timestamp get_time(String sorce){
        try{
            Pattern pattern = Pattern.compile("\\d+");  // 匹配一个或多个数字
            Matcher matcher = pattern.matcher(sorce);
            List<String> times = new ArrayList<>();

            while (matcher.find()) {
                String number = matcher.group();
                times.add(number);
            }
            String datetime = "2024-"+times.get(0)+"-"+times.get(1)+" "+times.get(2)+":"+times.get(3)+":00";
            return Timestamp.valueOf(datetime);
        }catch (Exception e){
            return null;
        }
    }
    private boolean check_type_one(AccessibilityNodeInfo order_node,float distance,float licheng){
        if(distance<(mWorkConfig.getMax_distance_one()*1000)){
            mLogInterface.logShow("一档距离符合:"+distance);
            AccessibilityNodeInfo money_node = findFirstNodeById(order_node,mWorkId.getTv_expect_money());
            float money = Float.parseFloat(MyTool.char2string(money_node.getText()));
            mLogInterface.logShow("订单：金额:"+money);
            if(money>=mWorkConfig.getMin_money_one()){
                mLogInterface.logShow("一档金额符合:"+money);
                if (mWorkConfig.getDanjia_one()>0&&(money / (licheng/1000) < mWorkConfig.getDanjia_one())) {
                    mLogInterface.logShow("一档单价不符合:"+(money / (licheng/1000)) + " | 设置值:"+mWorkConfig.getDanjia_one());
                    return false;
                }
                AccessibilityNodeInfo time_node = findFirstNodeById(order_node,mWorkId.getBooking_time());
                String time_str = MyTool.char2string(time_node.getText());
                if(!time_str.isEmpty()){
                    Timestamp timestamp = get_time(time_str);
                    if(timestamp==null){
                        mLogInterface.logShow("时间解析错误"+time_str);
                        return false;
                    }
                    long differenceTime = timestamp.getTime()-new Date().getTime();
                    if(differenceTime<(mWorkConfig.getTime_minute_one()*60000L)){
                        mLogInterface.logShow("一档时间符合:"+time_str);
                        AccessibilityNodeInfo from_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_start_address());
                        String from_str = MyTool.char2string(from_node.getText());
                        if(!from_str.isEmpty()){
                            boolean from_result = true;
                            for(String word:fromWordsone){
                                if(from_str.contains(word)){
                                    from_result = false;
                                    break;
                                }
                            }
                            if(from_result){
                                mLogInterface.logShow("一档起点符合:"+from_str);
                                AccessibilityNodeInfo to_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_end_address());
                                String to_str = MyTool.char2string(to_node.getText());
                                if(!to_str.isEmpty()){
                                    boolean to_result = true;
                                    for(String word:toWordsone){
                                        if(to_str.contains(word)){
                                            to_result = false;
                                            break;
                                        }
                                    }
                                    if(to_result){
                                        mLogInterface.logShow("一档终点符合:"+to_str);
                                        return to_result;
                                    }
                                }

                            }
                        }

                    }else{
                        mLogInterface.logShow("一档时间不符合:"+time_str + " | 设置值:"+mWorkConfig.getTime_minute_one());
                    }
                }

            }else{
                mLogInterface.logShow("一档金额不符合:"+money + " | 设置值:"+mWorkConfig.getMin_money_one());
            }
        }else{
            mLogInterface.logShow("一档距离不符合:"+distance + " | 设置值:"+mWorkConfig.getMax_distance_one());
        }
        return false;
    }
    private boolean check_type_two(AccessibilityNodeInfo order_node,float distance,float licheng){
        if(distance<(mWorkConfig.getMax_distance_two()*1000)){
            mLogInterface.logShow("二档距离符合:"+distance);
            AccessibilityNodeInfo money_node = findFirstNodeById(order_node,mWorkId.getTv_expect_money());
            float money = Float.parseFloat(MyTool.char2string(money_node.getText()));
            if(money>=mWorkConfig.getMin_money_two()){
                mLogInterface.logShow("二档金额符合:"+money);
                if (mWorkConfig.getDanjia_two()>0&&(money / (licheng/1000) < mWorkConfig.getDanjia_two())) {
                    mLogInterface.logShow("二档单价不符合:"+(money / (licheng/1000)) + " | 设置值:"+mWorkConfig.getDanjia_two());
                    return false;
                }
                AccessibilityNodeInfo time_node = findFirstNodeById(order_node,mWorkId.getBooking_time());
                String time_str = MyTool.char2string(time_node.getText());
                if(!time_str.isEmpty()){
                    Timestamp timestamp = get_time(time_str);
                    if(timestamp==null){
                        mLogInterface.logShow("时间解析错误"+time_str);
                        return false;
                    }
                    long differenceTime = timestamp.getTime()-new Date().getTime();
                    if(differenceTime<(mWorkConfig.getTime_minute_two()*60000L)){
                        mLogInterface.logShow("二档时间符合:"+time_str);
                        AccessibilityNodeInfo from_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_start_address());
                        String from_str = MyTool.char2string(from_node.getText());
                        if(!from_str.isEmpty()){
                            boolean from_result = true;
                            for(String word:fromWordstwo){
                                if(from_str.contains(word)){
                                    from_result = false;
                                    break;
                                }
                            }
                            if(from_result){
                                mLogInterface.logShow("一档起点符合:"+from_str);
                                AccessibilityNodeInfo to_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_end_address());
                                String to_str = MyTool.char2string(to_node.getText());
                                if(!to_str.isEmpty()){
                                    boolean to_result = true;
                                    for(String word:toWordstwo){
                                        if(to_str.contains(word)){
                                            to_result = false;
                                            break;
                                        }
                                    }
                                    if(to_result){
                                        mLogInterface.logShow("一档终点符合:"+to_str);
                                        return to_result;
                                    }
                                }

                            }
                        }

                    }else{
                        mLogInterface.logShow("二档时间不符合:"+time_str + " | 设置值:"+mWorkConfig.getTime_minute_two());
                    }
                }

            }else{
                mLogInterface.logShow("二档金额不符合:"+money + " | 设置值:"+mWorkConfig.getMin_money_two());
            }
        }else{
            mLogInterface.logShow("二档距离不符合:"+distance + " | 设置值:"+mWorkConfig.getMax_distance_two());
        }
        return false;
    }
    private boolean check_type_three(AccessibilityNodeInfo order_node,float distance,float licheng){
        if(distance<(mWorkConfig.getMax_distance_three()*1000)){
            mLogInterface.logShow("三档距离符合:"+distance);
            AccessibilityNodeInfo money_node = findFirstNodeById(order_node,mWorkId.getTv_expect_money());
            float money = Float.parseFloat(MyTool.char2string(money_node.getText()));
            if(money>=mWorkConfig.getMin_money_three()){
                mLogInterface.logShow("三档金额符合:"+money);
                if (mWorkConfig.getDanjia_three()>0&&(money / (licheng/1000) < mWorkConfig.getDanjia_three())) {
                    mLogInterface.logShow("三档单价不符合:"+(money / (licheng/1000)) + " | 设置值:"+mWorkConfig.getDanjia_three());
                    return false;
                }
                AccessibilityNodeInfo time_node = findFirstNodeById(order_node,mWorkId.getBooking_time());
                String time_str = MyTool.char2string(time_node.getText());
                if(!time_str.isEmpty()){
                    Timestamp timestamp = get_time(time_str);
                    if(timestamp==null){
                        mLogInterface.logShow("时间解析错误"+time_str);
                        return false;
                    }
                    long differenceTime = timestamp.getTime()-new Date().getTime();
                    if(differenceTime<(mWorkConfig.getTime_minute_three()*60000L)){
                        mLogInterface.logShow("三档时间符合:"+time_str);
                        AccessibilityNodeInfo from_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_start_address());
                        String from_str = MyTool.char2string(from_node.getText());
                        if(!from_str.isEmpty()){
                            boolean from_result = true;
                            for(String word:fromWordsthree){
                                if(from_str.contains(word)){
                                    from_result = false;
                                    break;
                                }
                            }
                            if(from_result){
                                mLogInterface.logShow("一档起点符合:"+from_str);
                                AccessibilityNodeInfo to_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_end_address());
                                String to_str = MyTool.char2string(to_node.getText());
                                if(!to_str.isEmpty()){
                                    boolean to_result = true;
                                    for(String word:toWordsthree){
                                        if(to_str.contains(word)){
                                            to_result = false;
                                            break;
                                        }
                                    }
                                    if(to_result){
                                        mLogInterface.logShow("一档终点符合:"+to_str);
                                        return to_result;
                                    }
                                }

                            }
                        }

                    }else{
                        mLogInterface.logShow("三档时间不符合:"+time_str + " | 设置值:"+mWorkConfig.getTime_minute_three());
                    }
                }

            }else{
                mLogInterface.logShow("三档金额不符合:"+money + " | 设置值:"+mWorkConfig.getMin_money_three());
            }
        }else{
            mLogInterface.logShow("三档距离不符合:"+distance + " | 设置值:"+mWorkConfig.getMax_distance_three());
        }
        return false;
    }
    private boolean check_type_four(AccessibilityNodeInfo order_node,float distance,float licheng){
        if(distance<(mWorkConfig.getMax_distance_four()*1000)){
            mLogInterface.logShow("四档距离符合:"+distance);
            AccessibilityNodeInfo money_node = findFirstNodeById(order_node,mWorkId.getTv_expect_money());
            float money = Float.parseFloat(MyTool.char2string(money_node.getText()));
            if(money>=mWorkConfig.getMin_money_four()){
                mLogInterface.logShow("四档金额符合:"+money);
                if (mWorkConfig.getDanjia_four()>0&&(money / (licheng/1000) < mWorkConfig.getDanjia_four())) {
                    mLogInterface.logShow("四档单价不符合:"+(money / (licheng/1000)) + " | 设置值:"+mWorkConfig.getDanjia_four());
                    return false;
                }
                AccessibilityNodeInfo time_node = findFirstNodeById(order_node,mWorkId.getBooking_time());
                String time_str = MyTool.char2string(time_node.getText());
                if(!time_str.isEmpty()){
                    Timestamp timestamp = get_time(time_str);
                    if(timestamp==null){
                        mLogInterface.logShow("时间解析错误"+time_str);
                        return false;
                    }
                    long differenceTime = timestamp.getTime()-new Date().getTime();
                    if(differenceTime<(mWorkConfig.getTime_minute_four()*60000L)){
                        mLogInterface.logShow("四档时间符合:"+time_str);
                        AccessibilityNodeInfo from_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_start_address());
                        String from_str = MyTool.char2string(from_node.getText());
                        if(!from_str.isEmpty()){
                            boolean from_result = true;
                            for(String word:fromWordsfour){
                                if(from_str.contains(word)){
                                    from_result = false;
                                    break;
                                }
                            }
                            if(from_result){
                                mLogInterface.logShow("一档起点符合:"+from_str);
                                AccessibilityNodeInfo to_node = findFirstNodeById(order_node,mWorkId.getTv_short_distance_end_address());
                                String to_str = MyTool.char2string(to_node.getText());
                                if(!to_str.isEmpty()){
                                    boolean to_result = true;
                                    for(String word:toWordsfour){
                                        if(to_str.contains(word)){
                                            to_result = false;
                                            break;
                                        }
                                    }
                                    if(to_result){
                                        mLogInterface.logShow("一档终点符合:"+to_str);
                                        return to_result;
                                    }
                                }

                            }
                        }

                    }else{
                        mLogInterface.logShow("四档时间不符合:"+time_str + " | 设置值:"+mWorkConfig.getTime_minute_four());
                    }
                }

            }else{
                mLogInterface.logShow("四档金额不符合:"+money + " | 设置值:"+mWorkConfig.getMin_money_four());
            }
        }else{
            mLogInterface.logShow("四档距离不符合:"+distance + " | 设置值:"+mWorkConfig.getMax_distance_four());
        }
        return false;
    }
    private boolean ordertype_isok(AccessibilityNodeInfo typeNode){
        if(typeNode==null){
            return false;
        }
        if(orderTypes.contains(MyTool.char2string(typeNode.getText()))){
            return true;
        }else{
            mLogInterface.logShow("订单类型不符合:"+MyTool.char2string(typeNode.getText()));
            return false;
        }
    }


    private AccessibilityNodeInfo findFirstNodeById(AccessibilityNodeInfo rootNode, String id){
        if(rootNode!=null){
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(id);
            for(AccessibilityNodeInfo node : nodes ){
                return node;
            }
        }

        return null;
    }
    private AccessibilityNodeInfo findFirstVisibleNodeById(AccessibilityNodeInfo rootNode, String id){
        if(rootNode!=null){
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(id);
            for(AccessibilityNodeInfo node : nodes ){
                if(node.isVisibleToUser()){
                    return node;
                }
            }
        }

        return null;
    }
    private List<AccessibilityNodeInfo> findAllNodeByText(AccessibilityNodeInfo info, String text) {
        List<AccessibilityNodeInfo> result = new ArrayList<>();
        if (info != null) {
            int count = info.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    AccessibilityNodeInfo subNode = info.getChild(i);
                    if (subNode == null) {
                        continue;
                    }
                    if (subNode.getText() != null) {
                        if (MyTool.char2string(subNode.getText()).equals(text)) {
                            result.add(subNode);
                        }
                    }
                    AccessibilityNodeInfo son_node = findFirstNodeByText(subNode, text);
                    if (son_node != null) {
                        result.add(son_node);
                    }
                }
            } else {
                if (MyTool.char2string(info.getText()).equals(text)) {
                    result.add(info);
                }
            }
        }
        return result;
    }
    private AccessibilityNodeInfo findFirstNodeByText(AccessibilityNodeInfo info, String text) {
        if (info != null) {
            int count = info.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    AccessibilityNodeInfo subNode = info.getChild(i);
                    if (subNode == null) {
                        continue;
                    }
                    if (subNode.getText() != null) {
                        if (MyTool.char2string(subNode.getText()).equals(text)) {
                            return subNode;
                        }
                    }
                    AccessibilityNodeInfo son_node = findFirstNodeByText(subNode, text);
                    if (son_node != null) {
                        return son_node;
                    }
                }
            } else {
                if (MyTool.char2string(info.getText()).equals(text)) {
                    return info;
                }
            }
        }
        return null;
    }
    private boolean native_click(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo!=null&&nodeInfo.isVisibleToUser()) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return false;
    }
    public boolean nativecoor_click(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo!=null&&nodeInfo.isVisibleToUser()) {
            if(!nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)){
                coordinateClick(nodeInfo);
            }
            return true;
        }
        return false;
    }
    public boolean coordinateClick(AccessibilityNodeInfo nodeInfo) {

        if (nodeInfo!=null&&nodeInfo.isVisibleToUser()) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            return tap_location(rect.centerX(), rect.centerY());
        }
        return false;
    }
    public boolean tap_location(int X, int Y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(X, Y);
            GestureDescription.Builder builder = new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path, 0, 20));
            return AutoService.mAutoService.dispatchGesture(builder.build(), null, null);
        }
        return false;
    }
}
