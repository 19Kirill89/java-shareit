package practic.shareit.item.comment.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import practic.shareit.item.comment.dto.CommentDto;
import practic.shareit.item.comment.model.Comment;
import practic.shareit.item.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(ItemMapper.toItemDto(comment.getItem()))
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }

    public static List<CommentDto> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}