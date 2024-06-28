package com.example.bcedu.Retrofit;

import com.example.bcedu.Model.BaiKiemTraModel;
import com.example.bcedu.Model.CauTracNghiemModel;
import com.example.bcedu.Model.CapDoTuVungModel;
import com.example.bcedu.Model.KetQuaKiemTraModel;
import com.example.bcedu.Model.KetQuaOnTapModel;
import com.example.bcedu.Model.MessageModel;
import com.example.bcedu.Model.NguoiDungModel;
import com.example.bcedu.Model.KhoaHocModel;
import com.example.bcedu.Model.ResponseModel;
import com.example.bcedu.Model.TuVungModel;
import com.example.bcedu.Model.XepHangModel;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiHocTap {
    // KHOÁ HỌC
    // Lấy tất cả thông tin trong bảng khoá học
    @GET("/bcedu/getkhoahoc.php")
    Observable<KhoaHocModel> getKhoaHocAPI();

    // Lấy danh sách học viên tham gia khoá học theo mã khoá học
    @POST("/bcedu/getdanhsachthamgiakhoahoc.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> getDanhSachThamGiaKhoaHocAPI(
            @Field("makhoahoc") int makhoahoc
    );

    // lấy thông tin giảng viên của khoá học theo mã khoá học
    @POST("/bcedu/getgiangvienkhoahoc.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> getGiangVien_API(
            @Field("makhoahoc") int makhoahoc
    );

    // Lấy thông tin các khoá học của 1 giảng viên: makhoahoc  và magiangvien
    @POST("/bcedu/getkhoahoctheomagiangvien.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> getKhoaHocTheoMaGiangVien_API(
            @Field("magiangvien") int magiangvien
    );

    // Insert 1 khoá học mới
    @POST("/bcedu/themkhoahoc.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> themKhoaHoc_API(
            @Field("magiangvien") int magiangvien,
            @Field("tenkhoahoc") String tenkhoahoc,
            @Field("ghichukhoahoc") String ghichukhoahoc,
            @Field("hinhanhmota") String hinhanhmota
    );

    // Xoá 1 khoá học
    @POST("/bcedu/xoakhoahoc.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> xoaKhoaHocAPI(
            @Field("makhoahoc") int makhoahoc
    );

    // sửa khoá học
    @POST("/bcedu/updatekhoahoc.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> suaKhoaHocAPI(
            @Field("tenkhoahoc") String tenkhoahoc,
            @Field("ghichukhoahoc") String ghichukhoahoc,
            @Field("hinhanhmota") String hinhanhmota,
            @Field("makhoahoc") int makhoahoc
    );

    // tham gia khoá học
    @POST("/bcedu/dangkikhoahoc.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> thamGiaKhoaHocAPI(
            @Field("makhoahoc") int makhoahoc,
            @Field("manguoidung") int manguoidung,
            @Field("ngaydangki") String ngaydangki
    );

    @POST("/bcedu/roikhoahoc.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> roiKhoaHocApi(
            @Field("makhoahoc") int makhoahoc,
            @Field("manguoidung") int manguoidung
    );

    //get khoá học đã tham gia của 1 học viên
    @POST("/bcedu/getkhoahoctheomahocvien.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> getKhoaHocTheoMaHocVien_API(
            @Field("manguoidung") int manguoidung
    );


    // TỪ VỰNG
    @GET("/bcedu/getcapdotuvung.php")
    Observable<CapDoTuVungModel> getCapDoTuVungAPI();

    @POST("/bcedu/getketquaontap.php")
    @FormUrlEncoded
    Observable<KetQuaOnTapModel> getKetQuaOnTapAPI(
            @Field("macapdo") int macapdo,
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/gettuvungtheocapdo.php")
    @FormUrlEncoded
    Observable<TuVungModel> getTuVungAPI(
            @Field("macapdotuvung") int macapdotuvung
    );

    @POST("/bcedu/postketquaontap.php")
    @FormUrlEncoded
    Observable<KetQuaOnTapModel> updateDiemCaoNhatAPI(
            @Field("diemcaonhat") float diemcaonhat,
            @Field("macapdo") int macapdo,
            @Field("manguoidung") int manguoidung,
            @Field("status") int status
    );

    @POST("/bcedu/postxephangmoi.php")
    @FormUrlEncoded
    Observable<KetQuaOnTapModel> updateXepHangAPI(
            @Field("maxephang") int maxephang,
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/xoatuvung.php")
    @FormUrlEncoded
    Observable<KhoaHocModel> xoaTuVungAPI(
            @Field("matuvung") int matuvung
    );

    @POST("/bcedu/themtuvung.php")
    @FormUrlEncoded
    Observable<KetQuaOnTapModel> themTuVung(
            @Field("macapdotuvung") int macapdotuvung,
            @Field("tentuvung") String tentuvung,
            @Field("loaituvung") String loaituvung,
            @Field("ynghia") String ynghia,
            @Field("phatam") String phatam,
            @Field("viduminhhoa") String viduminhhoa,
            @Field("hinhanhmota") String hinhanhmota,
            @Field("audio") String audio

    );

    @POST("/bcedu/updatetuvung.php")
    @FormUrlEncoded
    Observable<KetQuaOnTapModel> suaTuVung(
            @Field("matuvung") int matuvung,
            @Field("tentuvung") String tentuvung,
            @Field("loaituvung") String loaituvung,
            @Field("ynghia") String ynghia,
            @Field("phatam") String phatam,
            @Field("viduminhhoa") String viduminhhoa,
            @Field("hinhanhmota") String hinhanhmota,
            @Field("audio") String audio

    );

    // BÀI KIỂM TRA

    // get bài kiểm tra của 1 khoá học
    @POST("/bcedu/getbaikiemtratheomakhoahoc.php")
    @FormUrlEncoded
    Observable<BaiKiemTraModel> getBaiKiemTra_theoMaKhoaHoc(
            @Field("makhoahoc") int makhoahoc
    );

    //get dữ liệu bài kiểm tra theo mã bài kiểm tra
    @POST("/bcedu/getbaikiemtratheomabaikiemtra.php")
    @FormUrlEncoded
    Observable<BaiKiemTraModel> getBaiKiemTra_theoMaBaiKiemTra(
            @Field("mabaikiemtra") int mabaikiemtra
    );

    // thêm 1 bài kiểm tra
    @POST("/bcedu/thembaikiemtra.php")
    @FormUrlEncoded
    Observable<BaiKiemTraModel> themBaiKiemTraAPI(
            @Field("makhoahoc") int makhoahoc,
            @Field("tenbaikiemtra") String tenbaikiemtra,
            @Field("hinhanhminhhoa") String hinhanhminhhoa,
            @Field("thoiluong") int thoiluong
    );

    // xoá bài kiểm tra
    @POST("/bcedu/xoabaikiemtra.php")
    @FormUrlEncoded
    Observable<BaiKiemTraModel> xoaBaiKiemTraAPI(
            @Field("mabaikiemtra") int mabaikiemtra
    );

    // get danh sách các bài kiểm tra của 1 giảng viên
    @POST("/bcedu/getbaikiemtratheomanguoidung.php")
    @FormUrlEncoded
    Observable<BaiKiemTraModel> getBaiKiemTraTheoNguoiDung(
            @Field("manguoidung") int manguoidung
    );

    // update thông tin bài kiểm tra
    @POST("/bcedu/updatebaikiemtra.php")
    @FormUrlEncoded
    Observable<BaiKiemTraModel> capnhatBaiKiemTraAPI(
            @Field("tenbaikiemtra") String tenbaikiemtra,
            @Field("hinhanhminhhoa") String hinhanhminhhoa,
            @Field("thoiluong") int thoiluong,
            @Field("mabaikiemtra") int mabaikiemtra
    );

    // update điểm cao nhất của bài kiểm tra
    @POST("/bcedu/updatediemtracnghiem.php")
    @FormUrlEncoded
    Observable<BaiKiemTraModel> capnhatDiemCaoNhatBaiKiemTraAPI(
            @Field("diemso") float diemso,
            @Field("maketquakiemtra") int maketquakiemtra
    );

    // lấy thông tin điểm số của 1 học viên
    @POST("/bcedu/getdiemkiemtra.php")
    @FormUrlEncoded
    Observable<KetQuaKiemTraModel> getDiemKiemTra_API(
            @Field("mahocvien") int mahocvien,
            @Field("mabaikiemtra") int mabaikiemtra
    );


    // BÀI TRẮC NGHIỆM

    // lấy dữ liệu câu trắc nghiệm theo mã bài trắc nghiệm
    @POST("/bcedu/getcauhoitracnghiem.php")
    @FormUrlEncoded
    Observable<CauTracNghiemModel> getCauHoiTracNghiem(
            @Field("mabaikiemtra") int mabaikiemtra
    );

    // xoá câu trắc nghiệm
    @POST("/bcedu/xoacautracnghiem.php")
    @FormUrlEncoded
    Observable<CauTracNghiemModel> xoaCauHoiTracNghiemAPI(
            @Field("macautracnghiem") int macautracnghiem
    );

    // thêm câu trắc nghiệm
    @POST("/bcedu/themcautracnghiem.php")
    @FormUrlEncoded
    Observable<CauTracNghiemModel> themCauTracNghiemAPI(
            @Field("mabaikiemtra") int mabaikiemtra,
            @Field("cauhoi") String cauhoi,
            @Field("dapana") String dapana,
            @Field("dapanb") String dapanb,
            @Field("dapanc") String dapanc,
            @Field("dapand") String dapand,
            @Field("dapandung") String dapandung
    );

    @POST("/bcedu/updatecautracnghiem.php")
    @FormUrlEncoded
    Observable<CauTracNghiemModel> suaCauTracNghiemAPI(
            @Field("cauhoi") String cauhoi,
            @Field("dapana") String dapana,
            @Field("dapanb") String dapanb,
            @Field("dapanc") String dapanc,
            @Field("dapand") String dapand,
            @Field("dapandung") String dapandung,
            @Field("macautracnghiem") int macautracnghiem
    );

    // NGƯỜI DÙNG
    @POST("/bcedu/dangki.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> dangKiNguoiDungAPI(
            @Field("email") String email,
            @Field("tennguoidung") String tennguoidung,
            @Field("matkhau") String matkhau
    );

    @POST("/bcedu/dangnhap.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> dangNhapAPI(
            @Field("email") String email,
            @Field("matkhau") String matkhau
    );

    @GET("/bcedu/getallnguoidung.php")
    Observable<NguoiDungModel> getAllNguoiDungAPI();

    @POST("/bcedu/quenmatkhau.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> resetMatKhauAPI(
            @Field("email") String email
    );


    @POST("/bcedu/bannguoidung.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> banNguoiDung_API(
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/unbannguoidung.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> unbanNguoiDung_API(
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/getxephang.php")
    @FormUrlEncoded
    Observable<XepHangModel> getXepHangAPI(
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/getxephangmoi.php")
    @FormUrlEncoded
    Observable<XepHangModel> getXepHangMoiAPI(
            @Field("manguoidung") int manguoidung
    );

    // trả về thông tin của người dùng
    @POST("/bcedu/updatethongtincanhan.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> updateThongTinNguoiDungAPI(
            @Field("tennguoidung") String tennguoidung,
            @Field("email") String email,
            @Field("sodienthoai") String sodienthoai,
            @Field("anhdaidien") String anhdaidien,
            @Field("gioitinh") String gioitinh,
            @Field("ngaysinh") String ngaysinh,
            @Field("vaitro") String vaitro,
            @Field("manguoidung") int manguoidung
    );

    // get list người dùng theo mã xếp hạng (lấy theo rank)
    @POST("/bcedu/getnguoidungtheomaxephang.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> getNguoiDungTheoMaXepHang(
            @Field("maxephang") int maxephang
    );

    @FormUrlEncoded
    @POST("/bcedu/gettongdiemnguoidung.php")
    Observable<ResponseModel> getTotalDiem_API(
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/donehdmain.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> hoanThanhHuongDanMain_API(
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/donehdcapdotuvung.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> hoanThanhHuongDanCDTV_API(
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/donehdchitiettuvung.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> hoanThanhHuongDanCTTV_API(
            @Field("manguoidung") int manguoidung
    );

    @POST("/bcedu/caidatnhac.php")
    @FormUrlEncoded
    Observable<NguoiDungModel> caiDatNhac_API(
            @Field("manguoidung") int manguoidung,
            @Field("amthanh") int amthanh

    );


    // UPLOAD ẢNH
    @Multipart
    @POST("/bcedu/upload.php")
    Call<MessageModel> uploadFile(@Part MultipartBody.Part file);
}
