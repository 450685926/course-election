package com.server.edu.election.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.edu.session.util.SessionUtils;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @Description: 从beaseservice copy过来。我也不知道他为什么不放入共用包中。反正就这样吧，拉鸡巴倒
 * @author kan yuanfeng
 * @date 2019/11/7 9:39
 */
public class CommonConstant {
    public final static ObjectMapper mapper = new ObjectMapper();

    public static final String baseresservice = "用户管理";
    
    /**
     * 菜单权限
     */
    public static final int REL_TYPE_1 = 1;
    
    /**
     * 部门
     */
    public static final int REL_TYPE_2 = 2;
    
    public static final String PAY_URL = "http://paycwc.tongji.edu.cn/payment/pay/payment_appPay.action";
    
    public static final String PAY_FIND_ORDER_URL = "http://paycwc.tongji.edu.cn/payment/portal/Query_PayQuery.action";
    
    /**
     * 证书
     */
    public static final String PAY_CODE_CERT = "cert";
    
    /**
     * 所属系统ID
     */
    public static final String PAY_CODE_SYSID = "sysid";
    
    /**
     * 所属系统ID
     */
    public static final String PAY_CODE_AMOUNT = "amount";
   
    /**
     * 所属系统ID
     */
    public static final String PAY_CODE_CSEURL = "cseUrl";
    /**
     * 学生Id
     */
    public static final String PAY_CODE_STUID = "studentId";
    
    /**
     * 所属系统ID
     */
    public static final String PAY_SUCCESS = "1";
    
    /**
     * pay模块 redis key前缀
     */
    public static final String PAY_BILLNO = "pay_";
    
    public static final String UNDER_LINE = "_";
    
    /**
     * redis失效时长
     */
    public static final long NUMBER_TIMEOUT = 86400;
    
    /**
     * 所属子系统id
     */
    public static final String PAY_CODE_SUBSYSID = "subsysid";
    
    /**
     * 同步地址
     */
    public static final String PAY_CODE_NOTIFYURL = "notifyUrl";
    
    /**
     * 缴费项目代码
     */
    public static final String PAY_CODE_FEEITEMID = "feeitemid";
    
    /**
     * 缴费签名
     */
    public static final String PAY_CODE_SIGN = "sign";
    
    /**回调数据*/
    public static final String PAY_CALLBACKDATA = "callBackData";
    
    /**
     * 缴费项目代码
     */
    public static final String PAY_CODE_BILLNO = "billno";
    
    /**
     * 缴费项目代码
     */
    public static final String PAY_CODE_PAYSTATE = "paystate";
    
    /**
     * 回调标识：1回调成功，0回调失败
     */
    public static final String PAY_IS_CALL_BACK = "1";
    
    /**
     * 缴费项目代码
     */
    public static final String PAY_CODE_RETURNMSG = "returnmsg";
    
    /**
     * 支付状态： 缴费中
     */
    public static final int PAY_STATE_PAYING = -1;
    
    public static final String PAY_FEEITEM_CODE_1 = "JWC-02";
    
    public static final String PAY_FEEITEM_CODE_2 = "jwc4-02";
    
    public static final String PAY_FEEITEM_CODE_3 = "jwc3-01";
    
    public static final String PAY_FEEITEM_CODE_4 = "JWC-01";

    public static final String PAY_FEEITEM_CODE_5 = "jwc6-01";


    /**
     *  文件大小size
     */
     public static final int FILE_SIZE_THREE = 3;

    /**
     * 文件大小单位
     */
    public static final String FILE_SIZE_UNIT = "m";

    /**
     * 文件格式后缀名
     */
    public static final String FILE_TYPE_XLS = "xls";
    public static final String FILE_TYPE_XLSX = "xlsx";

    /**
     * excel文件 解析起始行数,起始列数
     */
    public static final int START_ROW_INDEX = 2;
    public static final int START_ROW_COL = 0;
    /**
     * excel文件 名称大小限制
     */
    public static final int NAME_MAX_LENGTH = 32;
    public static final int NAME_MAX_LENGTH2 = 256;

    /**
     * 管理部门
     */
    public static final String PROJECT_1 = "1";
    public static final String PROJECT_2 = "2";
    public static final String PROJECT_4 = "4";

    
    /**
     * <生成csv文件之前特殊字符转义>
     * 
     * @param str
     * @return String
     * @exception:
     * @author: lifeiyue
     * @time:2018年5月14日 下午3:32:50
     */
    public static String csvString(String str)
    {
        
        if (str.contains(","))
        {
            str = str.replace("\"", "\"\"");
            str = "\"" + str + "\"";
        }
        
        return str;
    }
    
    /**
     * 
     * <转义正则特殊字符>
     * 
     * @param keyword
     * @return String
     * @exception:
     * @author: lifeiyue
     * @time:2018年5月14日 下午5:23:18
     */
    public static String escapeExprSpecialWord(String keyword)
    {
        if (null != keyword && keyword.equals(""))
        {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]",
                "?", "^", "{", "}", "|"};
            for (String key : fbsArr)
            {
                if (keyword.contains(key))
                {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
    
    /**
     * 判断是否为数字
     * 
     * @param str
     * @return
     */
    public static boolean isInteger(String str)
    {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    
    public static boolean isEmptyStr(String str)
    {
        if (null == str || str.equals(""))
        {
            return true;
        }
        return false;
    }
    
    public static boolean isEmptyList(List list)
    {
        if (null == list || list.size() == 0)
        {
            return true;
        }
        return false;
    }
    
    public static boolean isEmptyObj(Object obj)
    {
        if (null == obj)
        {
            return true;
        }
        return false;
    }

    /**
     * @author yidonge
     * 培养模块 数据分权专用过滤查询条件
     * @param projId
     * @return projId
     */
    public static String dataFilterByprojId (String projId) {

        //1 如果projId有值的，以projId为准则过滤条件
        if (CommonConstant.isEmptyStr(projId)) {
            //2 projId为空，以session中currentManageDptId为准则过滤条件
            projId = SessionUtils.getCurrentSession().getCurrentManageDptId();
            if (CommonConstant.isEmptyStr(projId)) {
                //3 projId,currentManageDptId都为空，以session中的manageDptIds为准 过滤条件
               String tempProjId = SessionUtils.getCurrentSession().getManageDptIds().toString();
               tempProjId = tempProjId.replace(" ","");
               projId = tempProjId.substring(1, tempProjId.length()-1);
            }
        }
        return projId;
    }


    /**
     * 根据2进制值获取开始时间
     * @param timeBit
     * @return
     */
    public static int getEndBit(Long timeBit) {
        char[] chars = Long.toBinaryString(timeBit).toCharArray();
        int value = 0;
        for (int i = 0; i<chars.length; i++) {
            if('1' == chars[i]){
                value = chars.length - i;
                break;
            }
        }
        return  value;
    }

    /**
     * 根据2进制值获取结束时间
     * @param timeBit
     * @return
     */
    public static int getStartBit(Long timeBit) {
        char[] chars = Long.toBinaryString(timeBit).toCharArray();
        int value = 0;
        for (int i = (chars.length-1); i>=0; i--) {
            if('1' == chars[i]){
                value = chars.length - i;
                break;
            }
        }
        return  value;
    }

    /**
     *  把list的tostring 重构， 去除括号[]
     * @param list
     * @return
     */
    public static String listToString(List<String> list){
        String listStr = list.toString();
        return listStr.substring(1,listStr.length() - 1);
    }

    /**
     * 生成uuuId，并去除-符号
     */
    public static String getUUID(){
        String uuidStr = UUID.randomUUID().toString();
        return uuidStr.replace("-","");
    }
}