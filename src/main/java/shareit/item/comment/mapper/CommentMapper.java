package shareit.item.comment.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import shareit.item.comment.dto.CommentDto;
import shareit.item.comment.model.Comment;
import shareit.item.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CommentMapper {
    private final ItemMapper itemMapper;

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(itemMapper.toItemDto(comment.getItem()))
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }

    public List<CommentDto> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}