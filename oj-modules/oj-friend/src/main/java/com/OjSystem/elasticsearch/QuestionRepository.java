package com.OjSystem.elasticsearch;

//import com.OjSystem.friend.domain.exam.elasticsearch.QuestionES;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface QuestionRepository extends ElasticsearchRepository<QuestionES,Long> {
//
//    /**
//     * 通过Difficulty进行查询
//     * @param difficulty
//     * @param pageable
//     * @return
//     */
//    Page<QuestionES> findQuestionByDifficulty(Integer difficulty, Pageable pageable);
//
//
//    /**
//     * 通过Title或Content和Difficulty进行查询
//     * @param keywordTitle
//     * @param keywordContent
//     * @param difficulty
//     * @param pageable
//     * @return
//     */
//    @Query("{\"bool\": {\"should\": [{ \"match\": { \"title\": \"?0\" } }, { \"match\": { \"content\": \"?1\" } }], \"minimum_should_match\": 1, \"must\": [{\"term\": {\"difficulty\": \"?2\"}}]}}")
//    Page<QuestionES> findByTitleOrContentAndDifficulty(String keywordTitle, String keywordContent,Integer difficulty,  Pageable pageable);
//
//
//    /**
//     * 通过Title或Content进行查询
//     * @param keywordTitle
//     * @param keywordContent
//     * @param pageable
//     * @return
//     */
//    @Query("{\"bool\": {\"should\": [{ \"match\": { \"title\": \"?0\" } }, { \"match\": { \"content\": \"?1\" } }], \"minimum_should_match\": 1}}")
//    Page<QuestionES> findByTitleOrContent(String keywordTitle, String keywordContent, Pageable pageable);
//
//}
