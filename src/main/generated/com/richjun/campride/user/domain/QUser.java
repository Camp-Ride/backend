package com.richjun.campride.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1683101011L;

    public static final QUser user = new QUser("user");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath role = createString("role");

    public final ListPath<com.richjun.campride.room.domain.Room, com.richjun.campride.room.domain.QRoom> rooms = this.<com.richjun.campride.room.domain.Room, com.richjun.campride.room.domain.QRoom>createList("rooms", com.richjun.campride.room.domain.Room.class, com.richjun.campride.room.domain.QRoom.class, PathInits.DIRECT2);

    public final StringPath socialLoginId = createString("socialLoginId");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

