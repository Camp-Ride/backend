package com.richjun.campride.room.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoom is a Querydsl query type for Room
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoom extends EntityPathBase<Room> {

    private static final long serialVersionUID = -2109200525L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoom room = new QRoom("room");

    public final StringPath departure = createString("departure");

    public final com.richjun.campride.global.location.domain.QLocation departureLocation;

    public final DateTimePath<java.time.LocalDateTime> departureTime = createDateTime("departureTime", java.time.LocalDateTime.class);

    public final StringPath destination = createString("destination");

    public final com.richjun.campride.global.location.domain.QLocation destinationLocation;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath leaderNickname = createString("leaderNickname");

    public final NumberPath<Integer> maxParticipants = createNumber("maxParticipants", Integer.class);

    public final ListPath<com.richjun.campride.user.domain.User, com.richjun.campride.user.domain.QUser> participants = this.<com.richjun.campride.user.domain.User, com.richjun.campride.user.domain.QUser>createList("participants", com.richjun.campride.user.domain.User.class, com.richjun.campride.user.domain.QUser.class, PathInits.DIRECT2);

    public final EnumPath<com.richjun.campride.room.domain.type.RoomType> roomType = createEnum("roomType", com.richjun.campride.room.domain.type.RoomType.class);

    public final StringPath title = createString("title");

    public QRoom(String variable) {
        this(Room.class, forVariable(variable), INITS);
    }

    public QRoom(Path<? extends Room> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoom(PathMetadata metadata, PathInits inits) {
        this(Room.class, metadata, inits);
    }

    public QRoom(Class<? extends Room> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.departureLocation = inits.isInitialized("departureLocation") ? new com.richjun.campride.global.location.domain.QLocation(forProperty("departureLocation")) : null;
        this.destinationLocation = inits.isInitialized("destinationLocation") ? new com.richjun.campride.global.location.domain.QLocation(forProperty("destinationLocation")) : null;
    }

}

