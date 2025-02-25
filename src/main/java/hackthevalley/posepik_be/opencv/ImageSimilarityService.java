package hackthevalley.posepik_be.opencv;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.TM_CCOEFF_NORMED;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_features2d.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageSimilarityService {
  static {
    // Bytedeco OpenCV 네이티브 라이브러리 로드
    org.bytedeco.javacpp.Loader.load(opencv_core.class);
  }

  public static void extractOutlineUrl(String url1, String url2) {

    Mat m1 = loadImageFromURL(url1);
    Mat m2 = loadImageFromURL(url2);
    Mat outline = extractOutline(m1, m2);

    // PNG 형식으로 저장 (투명 배경 유지)
    imwrite("/Users/sumin/Desktop/qwert.png", outline);
    System.out.println("✅ 윤곽선 이미지가 저장되었습니다: " + "/Users/sumin/Desktop");
  }

  public static Mat extractOutline(Mat original, Mat cutout) {
    //   원본과 누끼 이미지의 차이 계산
    Mat diff = new Mat();
    absdiff(original, cutout, diff);

    // 그레이스케일 변환 및 윤곽선 강조
    Mat gray = new Mat();
    cvtColor(diff, gray, COLOR_BGRA2GRAY);
    threshold(gray, gray, 30, 255, THRESH_BINARY);

    //  윤곽선 검출
    MatVector contours = new MatVector();
    Mat hierarchy = new Mat();
    findContours(gray, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    // 투명한 배경을 가진 새로운 이미지 생성 (BGRA)
    Mat transparent = new Mat(original.size(), CV_8UC4, new Scalar(0, 0, 0, 0));

    int x = transparent.size().width();
    int y = transparent.size().height();

    // 윤곽선을 흰색(255,255,255,255)으로 그림
    for (int i = 0; i < contours.size(); i++) {
      drawContours(transparent, contours, i, new Scalar(255, 255, 255, 255));
    }
    int width = transparent.size().width() - 4;
    int height = transparent.size().height() - 4;

    Rect roi = new Rect(2, 2, width, height);
    Mat croppedTransparent = new Mat(transparent, roi); // ROI 적용하여 자른 이미지 생성

    return croppedTransparent;
  }

  // URL에서 이미지를 로드하고 유사도 계산
  public static double calculateSimilarityFromURL(String url1, String url2) {

    try {
      URL imageUrl = new URL(url1);
      InputStream inputStream = imageUrl.openStream();
      System.out.println("URL 정상적으로 접근 가능: " + url1);
      inputStream.close();
    } catch (Exception e) {
      System.out.println("URL 접근 불가: " + url1 + " 에러: " + e.getMessage());
    }

    Mat img1 = loadImageFromURL(url1);
    Mat img2 = loadImageFromURL(url2);

    if (img1.empty() || img2.empty()) {
      System.out.println("이미지를 불러올 수 없습니다.");
      return 0;
    }

    double ssimScore = calculateSSIM(img1, img2);
    double orbScore = calculateORB(img1, img2);
    double histoScore = compareHistograms(img1, img2);

    System.out.println("SSIM 유사도: " + ssimScore * 100);
    System.out.println("ORB 특징 유사도: " + orbScore);
    System.out.println("Histo 특징 유사도: " + (histoScore + 1) * 100 / 2);

    double finalScore = (ssimScore * 20) + ((histoScore + 1) / 2 * 80);
    System.out.println("점수: " + finalScore);
    return finalScore;

    //    System.out.println("점수: " + ((100 * ssimScore)));

    //    System.out.println("히스토그램 유사도: " + histScore);

  }

  // URL에서 이미지를 다운로드하여 OpenCV Mat 객체로 변환
  public static Mat loadImageFromURL(String imageUrl) {
    try {
      URL url = new URL(imageUrl);
      BufferedImage bufferedImage = ImageIO.read(url);

      if (bufferedImage == null) {
        System.out.println("이미지를 로드할 수 없습니다: " + imageUrl);
        return new Mat();
      }

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", byteArrayOutputStream); // PNG 형식으로 변환
      byte[] imageBytes = byteArrayOutputStream.toByteArray();

      // ✅ BytePointer로 변환
      BytePointer bytePointer = new BytePointer(imageBytes);
      if (bytePointer.isNull()) {
        throw new RuntimeException("BytePointer 변환 실패 (NULL 포인터)");
      }

      // ✅ OpenCV imdecode() 사용하여 Mat 변환
      Mat img = opencv_imgcodecs.imdecode(new Mat(bytePointer), opencv_imgcodecs.IMREAD_COLOR);

      return img;
    } catch (Exception e) {
      System.out.println("URL에서 이미지 불러오기 실패: " + imageUrl);
      e.printStackTrace();
      return new Mat();
    }
  }

  // 📌 SSIM (Structural Similarity Index) 계산 함수
  public static double calculateSSIM(Mat img1, Mat img2) {
    try {
      if (img1 == null || img2 == null || img1.empty() || img2.empty()) {
        throw new RuntimeException("❌ 입력 이미지가 NULL이거나 비어 있음");
      }

      Mat result = new Mat();
      matchTemplate(img1, img2, result, TM_CCOEFF_NORMED);

      // ✅ 최댓값을 가져오기 위해 minMaxLoc 사용
      DoublePointer minVal = new DoublePointer(1); // ✅ NULL 방지 초기화
      DoublePointer maxVal = new DoublePointer(1); // ✅ NULL 방지 초기화
      Point minLoc = new Point();
      Point maxLoc = new Point();

      // ✅ 올바른 minMaxLoc 호출 (Mat이 비어있으면 예외 발생 가능)
      if (result.empty()) {
        throw new RuntimeException("❌ matchTemplate 결과가 비어 있음.");
      }
      opencv_core.minMaxLoc(result, minVal, maxVal, minLoc, maxLoc, new Mat());

      if (maxVal == null || maxVal.isNull()) {
        throw new RuntimeException("❌ SSIM 계산 실패: DoublePointer가 NULL입니다.");
      }

      return maxVal.get(0); // ✅ NULL 체크 후 값 반환
    } catch (Exception e) {
      System.err.println("❌ SSIM 계산 중 오류 발생: " + e.getMessage());
      return -1.0; // 실패 시 기본값 반환
    }
  }

  // 📌 ORB 특징 매칭을 이용한 유사도 계산
  @Autowired
  public static double calculateORB(Mat img1, Mat img2) {
    ORB orb = ORB.create();
    Mat descriptors1 = new Mat(), descriptors2 = new Mat();
    KeyPointVector keypoints1 = new KeyPointVector(), keypoints2 = new KeyPointVector();

    orb.detectAndCompute(img1, new Mat(), keypoints1, descriptors1);
    orb.detectAndCompute(img2, new Mat(), keypoints2, descriptors2);

    BFMatcher matcher = new BFMatcher(NORM_HAMMING, true);
    DMatchVector matches = new DMatchVector();
    matcher.match(descriptors1, descriptors2, matches);

    return matches.size();
  }

  // 📌 히스토그램 비교를 이용한 유사도 계산

  //  import static org.bytedeco.opencv.global.opencv_core.*;
  // import static org.bytedeco.opencv.global.opencv_imgproc.*;

  public static double compareHistograms(Mat img1, Mat img2) {
    Mat hsvImg1 = new Mat();
    Mat hsvImg2 = new Mat();

    // Convert images to HSV color space
    cvtColor(img1, hsvImg1, COLOR_BGR2HSV);
    cvtColor(img2, hsvImg2, COLOR_BGR2HSV);

    // Define parameters for histogram
    int hBins = 50;
    int sBins = 60;
    int[] histSize = {hBins, sBins};
    float[] rangesArray = {0f, 180f, 0f, 256f}; // Combined hRanges and sRanges
    FloatPointer ranges = new FloatPointer(rangesArray);
    int[] channels = {0, 1};

    Mat histImg1 = new Mat();
    Mat histImg2 = new Mat();

    // Compute the histograms
    calcHist(
        new MatVector(hsvImg1),
        new IntPointer(channels),
        new Mat(),
        histImg1,
        new IntPointer(histSize),
        ranges);
    calcHist(
        new MatVector(hsvImg2),
        new IntPointer(channels),
        new Mat(),
        histImg2,
        new IntPointer(histSize),
        ranges);

    // Normalize the histograms
    normalize(histImg1, histImg1, 0, 1, NORM_MINMAX, -1, new Mat());
    normalize(histImg2, histImg2, 0, 1, NORM_MINMAX, -1, new Mat());

    // Compare histograms using correlation method
    double similarity = compareHist(histImg1, histImg2, HISTCMP_CORREL);

    return similarity;
  }
}
