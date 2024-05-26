package com.hipoom.file.catalogue;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ZhengHaiPeng
 * @since 2024/5/18 22:48
 */
class CatalogueTest {

    @Test
    void initialOneWorkspace() {
        String workspace = "/Users/zhp/Workspace/test/catalogue";
        Catalogue.initialOneWorkspace(workspace);
        Catalogue.addBusiness(workspace, "test1");
        Catalogue.addBusiness(workspace, "test1");
        File file = Catalogue.getBusinessFile(workspace, "test1");
        System.out.println(file);
    }
}