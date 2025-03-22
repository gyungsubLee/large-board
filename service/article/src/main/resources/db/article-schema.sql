-- 게시글 스키마 생성
CREATE TABLE article (
    article_id bigint not null primary key,
    title varchar(100) not null,
    content varchar(3000)  not null,
    board_id bigint  not null,
    writer_id bigint not null,
    created_at datetime not null,
    modified_at datetime not null
);


-- 인덱스 생성
CREATE INDEX idx_board_id_article_id ON article(board_id, article_id);
