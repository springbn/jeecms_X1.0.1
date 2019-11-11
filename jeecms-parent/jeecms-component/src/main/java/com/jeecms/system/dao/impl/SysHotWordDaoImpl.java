/**   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.system.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jeecms.channel.domain.querydsl.QChannel;
import com.jeecms.common.jpa.QuerydslUtils;
import com.jeecms.common.page.Paginable;
import com.jeecms.system.dao.ext.SysHotWordDaoExt;
import com.jeecms.system.domain.SysHotWord;
import com.jeecms.system.domain.SysHotWordCategory;
import com.jeecms.system.domain.querydsl.QSysHotWord;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;

/**
 * 热词扩展dao实现类
 * 
 * @author: tom
 * @date: 2019年5月21日 下午4:39:49
 */
public class SysHotWordDaoImpl implements SysHotWordDaoExt {

        @Override
        public Page<SysHotWord> getPage(String hotWord, Integer siteId, Integer hotWordCategoryId, Integer channelId,
                        Pageable pageable) {
                JPAQuery<SysHotWord> query = new JPAQuery<SysHotWord>(this.em);
                QSysHotWord sysHotWord = QSysHotWord.sysHotWord;
                appendQuery(query, sysHotWord, hotWord, siteId, hotWordCategoryId, channelId);
                return QuerydslUtils.page(query, pageable, sysHotWord);
        }

        @Override
        public List<SysHotWord> getList(String hotWord, Integer siteId, Integer hotWordCategoryId, Integer channelId,
                        Paginable paginable) {
                JPAQuery<SysHotWord> query = new JPAQuery<SysHotWord>(this.em);
                QSysHotWord sysHotWord = QSysHotWord.sysHotWord;
                appendQuery(query, sysHotWord, hotWord, siteId, hotWordCategoryId, channelId);
                return QuerydslUtils.list(query, paginable, sysHotWord);
        }

        private void appendQuery(JPAQuery<SysHotWord> query, QSysHotWord qSysHotWord, String hotWord, Integer siteId,
                        Integer hotWordCategoryId, Integer channelId) {
                query.from(qSysHotWord);
                query.setHint(QueryHints.HINT_CACHEABLE, true);
                BooleanBuilder exp = new BooleanBuilder();
                if (channelId != null) {
                        QChannel channel = QChannel.channel;
                        query.innerJoin(qSysHotWord.sysHotWordCategory.channels, channel);
                        exp = exp.and(qSysHotWord.sysHotWordCategory.applyScope
                                        .eq(SysHotWordCategory.CATEGORY_RANGE_ALL))
                                        .or(channel.id.eq(channelId));
                }
                exp.and(qSysHotWord.hasDeleted.eq(false));
                if (StringUtils.isNoneBlank(hotWord)) {
                        exp.and(qSysHotWord.hotWord.like("%" + hotWord + "%"));
                }
                if (siteId != null) {
                        exp.and(qSysHotWord.siteId.eq(siteId));
                }
                if (hotWordCategoryId != null) {
                        exp.and(qSysHotWord.hotWordCategoryId.eq(hotWordCategoryId));
                }
                query.where(exp);
        }

        private EntityManager em;

        @javax.persistence.PersistenceContext
        public void setEm(EntityManager em) {
                this.em = em;
        }

}
