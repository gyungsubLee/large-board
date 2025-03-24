₩₩-- [ Depth: 2 ] 댓글 스키마 생성
CREATE TABLE comment (
    comment_id bigint not null primary key,
    content varchar(3000) not null,
    article_id bigint not null,
    writer_id bigint not null,
    parent_comment_id bigint not null,
    deleted bool not null,
    created_at datetime not null
);

-- [ Depth: 2 ] 인덱스 생성
CREATE INDEX idx_article_parent_comment ON comment(article_id, parent_comment_id, comment_id);



-- [ Depth: 무한 ] 댓글 스키마 생성
CREATE TABLE comment_v2 (
    comment_id bigint not null primary key,
    content varchar(3000) not null,
    article_id bigint not null,
    writer_id bigint not null,
    path varchar(25) character set utf8mb4 collate utf8mb4_bin not null,
    deleted bool not null,
    created_at datetime not null
);

-- [ Depth: 무한 ] 인덱스 생성
CREATE UNIQUE INDEX idx_article_id_path ON comment_v2(article_id ASC, path ASC);

