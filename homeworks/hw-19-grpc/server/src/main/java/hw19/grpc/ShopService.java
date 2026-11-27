package hw19.grpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ShopService extends ShopGrpc.ShopImplBase {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Product> products = new HashMap<>();
    private final Map<Long, Set<Long>> carts = new HashMap<>();
    private long lastUserId;
    private long lastProductId;

    @Override
    public void createUser(User request, StreamObserver<Id> responseObserver) {
        long id = ++lastUserId;
        User user = request.toBuilder().setId(id).build();
        users.put(id, user);
        responseObserver.onNext(Id.newBuilder().setValue(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void changeUserEmail(ChangeUserEmailRequest request, StreamObserver<User> responseObserver) {
        User user = users.get(request.getId());
        user = user.toBuilder().setEmail(request.getEmail()).build();
        users.put(user.getId(), user);
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }

    @Override
    public void changeUserName(ChangeUserNameRequest request, StreamObserver<User> responseObserver) {
        User user = users.get(request.getId());
        user = user.toBuilder().setUsername(request.getUsername()).build();
        users.put(user.getId(), user);
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(Product request, StreamObserver<Id> responseObserver) {
        long id = ++lastProductId;
        Product product = request.toBuilder().setId(id).build();
        products.put(id, product);
        responseObserver.onNext(Id.newBuilder().setValue(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addProductToCart(AddProductToCartRequest request, StreamObserver<Empty> responseObserver) {
        carts.computeIfAbsent(request.getUserId(), id -> new HashSet<>())
                .add(request.getProductId());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
