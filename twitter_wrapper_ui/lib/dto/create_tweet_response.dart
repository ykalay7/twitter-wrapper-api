import '../entites/twitter.dart';

class CreateTweetResponse{
  final Twitter tweet;

  CreateTweetResponse({required this.tweet});

  factory CreateTweetResponse.fromJson(Map<String, dynamic> json) {
    return CreateTweetResponse(tweet: Twitter(tweetId: json["tweet"]["tweetId"], message: json["tweet"]["message"]));
  }
}