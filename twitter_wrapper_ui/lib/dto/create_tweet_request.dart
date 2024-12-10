class CreateTweetRequest{
  final String message;

  CreateTweetRequest({required this.message});

  Map<String, dynamic> toJson() {
    return {
      'message':message
    };
  }
}