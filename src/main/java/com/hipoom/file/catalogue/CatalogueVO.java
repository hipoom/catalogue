package com.hipoom.file.catalogue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author ZhengHaiPeng
 * @since 2024/5/18 13:06
 */
public class CatalogueVO {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    @SerializedName("version")
    public int version;

    @SerializedName("mapping")
    public List<Mapping> mapping;

    @SerializedName("nextIndex")
    public int nextIndex = 0;



    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    public static CatalogueVO createDefault() {
        CatalogueVO vo = new CatalogueVO();
        vo.version = 1;
        vo.mapping = new LinkedList<>();
        vo.nextIndex = 0;
        return vo;
    }

    public Mapping findMapping(String businessNonnull) {
        List<Mapping> temp = mapping;
        if (temp == null) {
            return null;
        }

        for (Mapping mapping : temp) {
            if (businessNonnull.equals(mapping.business)) {
                return mapping;
            }
        }

        return null;
    }



    /* ======================================================= */
    /* Inner Class                                             */
    /* ======================================================= */

    public static class Mapping {

        @SerializedName("business")
        public String business;

        /**
         * 相对 workspace 的路径。
         */
        @SerializedName("relativePath")
        public String relativePath;

        public Mapping(String business, String relativePath) {
            this.business = business;
            this.relativePath = relativePath;
        }

        public String getAbsolutePath(String workspace) {
            return new File(workspace, relativePath).getAbsolutePath();
        }
    }
}
