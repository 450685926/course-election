package com.server.edu.exam.util;

import com.server.edu.exam.base.UndergExamBaseEntity;

import java.util.Date;

/**
 * 功能描述：本科生排考工具类
 *
 * @ClassName UndergExamUtil
 * @Author zhaoerhu
 * @Date 2019/8/19 17:26
 */
public class UndergExamUtil {
    public static Builder builderByParam(UndergExamBaseEntity undergExamBaseEntity) {
        return new Builder(undergExamBaseEntity);
    }

    public static class Builder {
        private UndergExamBaseEntity undergExamBaseEntity;

        public Builder(UndergExamBaseEntity undergExamBaseEntity) {
            this.setUndergExamBaseEntity(undergExamBaseEntity);
        }

        private UndergExamBaseEntity getUndergExamBaseEntity() {
            return undergExamBaseEntity;
        }

        private void setUndergExamBaseEntity(UndergExamBaseEntity undergExamBaseEntity) {
            this.undergExamBaseEntity = undergExamBaseEntity;
        }

        public Builder setCreateParam() {
            undergExamBaseEntity.setCreateDate(new Date());
            undergExamBaseEntity.setCreatePerson(null);
            return this;
        }

        public Builder setUpdateParam() {
            undergExamBaseEntity.setUpdateDate(new Date());
            undergExamBaseEntity.setUpdatePerson(null);
            return this;
        }

        public Builder setOtherParam() {
            undergExamBaseEntity.setSortTime(System.currentTimeMillis());
            return this;
        }

        public Builder setAllParam() {
            this.setCreateParam().setUpdateParam().setOtherParam();
            return this;
        }

        public UndergExamBaseEntity build() {
            return this.undergExamBaseEntity;
        }
    }
}
