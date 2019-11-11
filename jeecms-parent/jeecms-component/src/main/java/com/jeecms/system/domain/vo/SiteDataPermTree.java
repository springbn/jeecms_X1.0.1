/**   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.system.domain.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jeecms.system.domain.CmsSite;
import com.jeecms.system.domain.vo.CmsDataPermVo.MiniDataUnit;
import com.jeecms.system.domain.vo.CmsDataPermVo.SiteRow;

/**
 * 站点数据权限树
 * 
 * @author: tom
 * @date: 2019年4月27日 下午4:58:44
 */
public class SiteDataPermTree {
        CmsSite site;
        Integer id;
        String name;
        Integer parentId;
        List<SiteDataPermTree> children;
        List<MiniDataUnit> rowDatas;

        /**
         * 站点数据权限集合转换成带树形结构数据
         * 
         * @Title: getChildTree
         * @param sitePerms
         *                站点数据权限
         * @return List
         */
        public static List<SiteDataPermTree> getChildTree(Collection<SiteRow> sitePerms) {
                List<SiteDataPermTree> result = new ArrayList<SiteDataPermTree>();
                if (null == sitePerms || sitePerms.size() == 0) {
                        return result;
                }
                List<CmsSite> childs = sitePerms.stream().map(perm -> perm.getSite()).collect(Collectors.toList());
                Map<Integer, List<MiniDataUnit>> map = sitePerms.stream()
                                .collect(Collectors.toMap(SiteRow::getSiteId, SiteRow::getRowDatas));
                if (childs != null && !childs.isEmpty()) {
                        CmsSite site = childs.iterator().next();
                        Integer parentId = null;
                        if (site != null) {
                                parentId = site.getParentId();
                        }
                        List<SiteDataPermTree> dataSource = new ArrayList<>();
                        Map<Integer, SiteDataPermTree> hashDatas = new HashMap<>(childs.size());
                        for (CmsSite t : childs) {
                                SiteDataPermTree st = new SiteDataPermTree();
                                if (t != null) {
                                        st.setId(t.getId());
                                        st.setParentId(t.getParentId());
                                        st.setSite(t);
                                        st.setName(t.getName());
                                        st.setRowDatas(map.get(t.getId()));
                                }
                                // 没有子节点则过滤childs
                                long count = childs.stream().filter(c -> null != c.getParentId()
                                                && ((Integer) t.getId()).intValue() == c.getParentId().intValue())
                                                .count();
                                if (count > 0) {
                                        st.setChildren(new ArrayList<SiteDataPermTree>());
                                }
                                dataSource.add(st);
                                hashDatas.put(t.getId(), st);
                        }
                        childs.clear();

                        // 遍历菜单集合
                        for (int i = 0; i < dataSource.size(); i++) {
                                // 当前节点
                                SiteDataPermTree json = (SiteDataPermTree) dataSource.get(i);
                                // 当前的父节点
                                SiteDataPermTree hashObject = hashDatas.get(json.getParentId());

                                if (hashObject != null) {
                                        // 表示当前节点为子节点
                                        hashObject.getChildren().add(json);
                                } else if (null == json.getParentId()
                                                || parentId.intValue() == ((Integer) json.getParentId())) {
                                        // parentId为null和获取匹配parentId的节点(生成某节点的子节点树时需要用到)
                                        result.add(json);
                                }
                        }
                }
                return result;
        }

        public CmsSite getSite() {
                return site;
        }

        public Integer getId() {
                return id;
        }

        public Integer getParentId() {
                return parentId;
        }

        public List<SiteDataPermTree> getChildren() {
                return children;
        }
        

        /**
         * @return the name
         */
        public String getName() {
                return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
                this.name = name;
        }

        public void setSite(CmsSite site) {
                this.site = site;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        
        public void setParentId(Integer parentId) {
                this.parentId = parentId;
        }

        public void setChildren(List<SiteDataPermTree> children) {
                this.children = children;
        }

        /**
         * 获取站点操作数据
         * 
         * @Title: getRowDatas
         * @return List
         */
        public List<MiniDataUnit> getRowDatas() {
                return rowDatas;
        }

        public void setRowDatas(List<MiniDataUnit> rowDatas) {
                this.rowDatas = rowDatas;
        }

}
