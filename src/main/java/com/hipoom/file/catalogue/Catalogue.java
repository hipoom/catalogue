package com.hipoom.file.catalogue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.hipoom.Files;
import com.hipoom.Files.DstFileExistPolicy;
import com.hipoom.file.catalogue.CatalogueVO.Mapping;

/**
 * 文件目录管理。
 *
 * @author ZhengHaiPeng
 * @since 2024/5/18 22:10
 */
public class Catalogue {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    private static final String CATALOGUE_FILE_NAME = "catalogue.txt";



    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 初始化一个目录。
     * 如果目录中没有 catalogue.txt 文件，则创建并初始化 catalogue.txt 文件。
     * 如果有，则忽略。
     */
    public synchronized static boolean initialOneWorkspace(String workspace) {
        // 确保目录存在
        Files.ensureDirectory(new File(workspace));

        File catalogue = new File(workspace, CATALOGUE_FILE_NAME);

        // 如果 catalogue.txt 存在，读取内容，判断是否能正确读取
        if (catalogue.exists()) {
            // 读取文件内容
            CatalogueVO vo = loadCatalogue(catalogue);
            // 如果为 null，说明有文件，但没有内容，或者内容无法解析，需要重新初始化。
            if (vo == null) {
                return initialCatalogueFile(catalogue);
            }
            return true;
        }

        // 创建新文件，并写入初始内容
        int code = Files.createNewFileIfNotExist(catalogue);
        if (code != Files.CODE_SUCCESS) {
            return false;
        }

        // 初始化
        return initialCatalogueFile(catalogue);
    }

    /**
     * 新增一个业务文件到 workspace 中。
     */
    public synchronized static boolean addBusiness(String workspace, String business) {
        // 加载 catalogue.txt 文件
        File catalogue = new File(workspace, CATALOGUE_FILE_NAME);
        CatalogueVO vo = loadCatalogue(catalogue);

        // 如果无法解析，重新初始化
        if (vo == null) {
            boolean isSuccess = initialOneWorkspace(workspace);
            if (!isSuccess) {
                return false;
            }
            vo = loadCatalogue(catalogue);
        }

        // 即使重新初始化也无法读取，则放弃
        if (vo == null) {
            return false;
        }

        // 判断 business 是否已经存在了，如果已经存在了，则不再新增，直接返回。
        Mapping mapping = vo.findMapping(business);
        if (mapping != null && new File(mapping.getAbsolutePath(workspace)).exists()) {
            return true;
        }

        // 目标 business 的文件
        File businessFile = new File(workspace, "catalogue-" + vo.nextIndex + ".txt");
        int code = Files.createNewFileIfNotExist(businessFile);
        if (Files.CODE_SUCCESS != code) {
            return false;
        }

        // 加入到目录内容中
        if (vo.mapping == null) {
            vo.mapping = new LinkedList<>();
        }
        String businessPath = businessFile.getAbsolutePath();
        String relativeBusinessPath = businessPath.substring(workspace.length());
        vo.mapping.add(new Mapping(business, relativeBusinessPath));
        vo.nextIndex = vo.nextIndex + 1;

        // 转为 String 写入文件
        String newJson = new Gson().toJson(vo);
        code = Files.writeText(catalogue, newJson, DstFileExistPolicy.Overwrite);
        return code == Files.CODE_SUCCESS;
    }

    /**
     * 获取业务对应的文件。
     */
    public synchronized static File getBusinessFile(String workspace, String business) {
        // 加载 catalogue.txt 文件
        File catalogue = new File(workspace, CATALOGUE_FILE_NAME);
        CatalogueVO vo = loadCatalogue(catalogue);
        if (vo == null) {
            return null;
        }

        Mapping mapping = vo.findMapping(business);
        if (mapping == null) {
            return null;
        }

        String filePath = mapping.getAbsolutePath(workspace);
        return new File(filePath);
    }

    /**
     * 获取 workspace 中有哪些 business。
     * 如果 workspace 不是一个由 Catalogue 管理的文件夹，则返回空数组。
     */
    public synchronized static List<String> getAllBusinessName(String workspace) {
        List<String> names = new LinkedList<>();

        // 加载 catalogue.txt 文件
        File catalogue = new File(workspace, CATALOGUE_FILE_NAME);
        CatalogueVO vo = loadCatalogue(catalogue);
        if (vo == null) {
            return names;
        }

        if (vo.mapping == null) {
            return names;
        }

        for (Mapping mapping : vo.mapping) {
            names.add(mapping.business);
        }

        return names;
    }



    /* ======================================================= */
    /* Private Methods                                         */
    /* ======================================================= */

    /**
     * 读取 catalogue.txt 文件，并解析为 CatalogueVO 对象。
     * @return 如果文件不存在，返回 null； 如果文件的内容是空，或者解析内容失败，则返回 null；否则，返回对应的 CatalogueVO.
     */
    private static CatalogueVO loadCatalogue(File catalogueFile) {
        String txt = Files.readText(catalogueFile);
        if (txt == null || txt.isEmpty()) {
            return null;
        }
        try {
            return new Gson().fromJson(txt, CatalogueVO.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 初始化 catalogue 文件。
     *
     * @param ensuredCatalogueFile 一个已经存在的 catalogue.txt 文件。
     */
    private static boolean initialCatalogueFile(File ensuredCatalogueFile) {
        CatalogueVO vo = CatalogueVO.createDefault();
        String newJson = new Gson().toJson(vo);
        int code = Files.writeText(ensuredCatalogueFile, newJson, DstFileExistPolicy.Overwrite);
        return code == Files.CODE_SUCCESS;
    }

}
