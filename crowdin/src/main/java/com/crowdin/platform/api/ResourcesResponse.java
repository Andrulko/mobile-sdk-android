package com.crowdin.platform.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ResourcesResponse {

    @SerializedName("data")
    private List<LanguageData> dataList;

    public List<LanguageData> getDataList() {
        return dataList;
    }

    public class LanguageData {

        private String language;
        private int version;
        @SerializedName("app_version")
        private int appVersion;
        private Map<String, String> resources;
        private List<StringArray> arrays;

        public String getLanguage() {
            return language;
        }

        public int getVersion() {
            return version;
        }

        public int getAppVersion() {
            return appVersion;
        }

        public Map<String, String> getResources() {
            return resources;
        }

        public List<StringArray> getArrays() {
            return arrays;
        }
    }

    public class StringArray {

        private String name;
        private List<String> values;

        public String getName() {
            return name;
        }

        public List<String> getValues() {
            return values;
        }
    }
}