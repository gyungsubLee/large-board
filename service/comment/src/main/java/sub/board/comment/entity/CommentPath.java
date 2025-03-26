package sub.board.comment.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentPath {
    private String path;

    // 62자: 10(숫자) + 26(대문자) + 26(소문자)
    public static final String CHARSET ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqwstuvwxyz";

    public static final int DEPTH_CHUNK_SIZE = 5;
    public static final int MAX_DEPTH = 5;

    // MIN_CHUNK = "00000" , MAX_CHUNK = "zzzzz"
    private static final String MIN_CHUNK = String.valueOf(CHARSET.charAt(0)).repeat(DEPTH_CHUNK_SIZE);
    private static final String MAX_CHUNK = String.valueOf(CHARSET.charAt(CHARSET.length() - 1)).repeat(DEPTH_CHUNK_SIZE);

    public static CommentPath create(String path) {
        if (isDepthOverFlowed(path)) {
            throw new IllegalArgumentException("depth overflowed");
        }
        CommentPath commentPath = new CommentPath();
        commentPath.path = path;
        return commentPath;
    }

    private static boolean isDepthOverFlowed(String path) {
        return calDepth(path) > MAX_DEPTH;
    }

    private static int calDepth(String path) {
        return path.length() / DEPTH_CHUNK_SIZE;
    }

    public int getDepth() {
        return calDepth(path);
    }

    public boolean isRoot() {
        return calDepth(path) == 1;
    }

    public String getParentPath() {
        return path.substring(0, path.length() - DEPTH_CHUNK_SIZE);  // 마지막 최하위 chunk 5 개를 제외한 잎의 모든 문자열, 최상위 노드인 경우 존재 X
    }

    public CommentPath createChildCommentPath(String descendantsTopPath) {
        if (descendantsTopPath == null) {
            return CommentPath.create(path + MIN_CHUNK);
        }

        String childrenTopPath = findChildrenTopPath(descendantsTopPath);
        return CommentPath.create(increase(childrenTopPath));
    }

    // Notes: getDepth()는 descendantsTopPath의 depth가 아닌, CommentPath의 path 값이다.
    private String findChildrenTopPath(String descendantsTopPath) {
        return descendantsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE);
    }

    private String increase(String path) {
        String lastChunk = path.substring(path.length() - DEPTH_CHUNK_SIZE);

        if (isChunkOverflowed(lastChunk)) {
            throw new IllegalArgumentException("chunk overflowed");
        }

        int charsetLength = CHARSET.length();

        int value = 0;
        for (char ch : lastChunk.toCharArray()) {
            value = value * charsetLength + CHARSET.indexOf(ch);
        }

        value += 1;

        StringBuilder result = new StringBuilder();
        for(int i=0; i < DEPTH_CHUNK_SIZE; i++) {
            result.insert(0, CHARSET.charAt(value % charsetLength));
            value /= charsetLength;
        }
        return path.substring(0, path.length() - DEPTH_CHUNK_SIZE) + result;
    }

    private boolean isChunkOverflowed(String lastChunk) {
        return MAX_CHUNK.equals(lastChunk);
    }
}
