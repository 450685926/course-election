package com.server.edu.exam.enums;

/**
 * 功能描述：本科生排考通用枚举类
 *
 * @ClassName UndergExamCommonEnum
 * @Author zhaoerhu
 * @Date 2019/8/19 17:08
 */
public class UndergExamCommonEnum {
    /**
     * 功能描述: 逻辑删除标志字段内部枚举
     *
     * @params:
     * @return:
     * @author: zhaoerhu
     * @date: 2019/8/19 17:09
     */
    public enum DeletedEnum {
        NOT_DELETED(0, "未删除"), DELETED(1, "已删除");
        private Integer code;
        private String value;

        private DeletedEnum(Integer code, String value) {
            setCode(code);
            setValue(value);
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 功能描述:本科生排考开关类型内部枚举
     *
     * @params:
     * @return:
     * @author: zhaoerhu
     * @date: 2019/8/19 17:12
     */
    public enum ConfigTypeEnum {
        SELF_EXAM_SWITCH_FLAG(1, "自排标记开关"), SELF_EXAM_SWITCH(2, "自排开关"), ENTRY_EXAM_SWITCH(3, "录入监考开关"),
        QUERY_EXAM_SWITCH(4, "排考查询开关"), APPLICATION_EXAM_SWITCH(5, "学生补考申请开关");
        private Integer code;
        private String value;

        private ConfigTypeEnum(Integer code, String value) {
            setCode(code);
            setValue(value);
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        /**
         * 功能描述:验证code是否存在
         *
         * @params: [code]
         * @return: java.lang.Boolean
         * @author: zhaoerhu
         * @date: 2019/8/20 9:54
         */
        public static Boolean checkCodeExist(Integer code) {
            for (ConfigTypeEnum configTypeEnum : ConfigTypeEnum.values()) {
                if (configTypeEnum.getCode() == code) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    }

    /**
     * 功能描述: 本科生排考考试类型内部枚举
     *
     * @params:
     * @return:
     * @author: zhaoerhu
     * @date: 2019/8/20 11:13
     */
    public enum ExamTypeEnum {
        FINAL_EXAM(1, "期末考试"), MAKE_EXAM(2, "补考");
        private Integer code;
        private String value;

        private ExamTypeEnum(Integer code, String value) {
            setCode(code);
            setValue(value);
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        /**
         * 功能描述:验证code是否存在
         *
         * @params: [code]
         * @return: java.lang.Boolean
         * @author: zhaoerhu
         * @date: 2019/8/20 9:54
         */
        public static Boolean checkCodeExist(Integer code) {
            for (ExamTypeEnum examTypeEnum : ExamTypeEnum.values()) {
                if (examTypeEnum.getCode() == code) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    }
}
