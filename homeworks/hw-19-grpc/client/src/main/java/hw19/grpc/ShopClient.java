package hw19.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class ShopClient {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(args[0], Integer.parseInt(args[1]))
                .usePlaintext()
                .build();
        try {
            ShopGrpc.ShopBlockingStub stub = ShopGrpc.newBlockingStub(channel);

            String email = "ivan@example.com";
            String username = "ivan";
            long userId = stub.createUser(User.newBuilder().setEmail(email).setUsername(username).build()).getValue();
            System.out.println("createUser(" + email + ", " + username + ") -> id " + userId);

            String newEmail = "petrov@example.com";
            User user = stub.changeUserEmail(ChangeUserEmailRequest.newBuilder().setId(userId).setEmail(newEmail).build());
            System.out.println("changeUserEmail(" + userId + ", " + newEmail + ") -> " + describe(user));

            String newUsername = "petrov";
            user = stub.changeUserName(ChangeUserNameRequest.newBuilder().setId(userId).setUsername(newUsername).build());
            System.out.println("changeUserName(" + userId + ", " + newUsername + ") -> " + describe(user));

            String productName = "coffee";
            long productId = stub.createProduct(Product.newBuilder().setName(productName).build()).getValue();
            System.out.println("createProduct(" + productName + ") -> id " + productId);

            stub.addProductToCart(AddProductToCartRequest.newBuilder().setUserId(userId).setProductId(productId).build());
            System.out.println("addProductToCart(" + userId + ", " + productId + ") -> ok");
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static String describe(User user) {
        return "email: " + user.getEmail() + ", username: " + user.getUsername() + ", id: " + user.getId();
    }
}
