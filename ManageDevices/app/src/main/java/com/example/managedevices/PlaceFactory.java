package com.example.managedevices;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.managedevices.Database.ConnectToDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class PlaceFactory extends AppCompatActivity {
    //Tạo biến để kết nối
    Connection connection;
    //Khai báo mảng Array List cho LisView của Khu Vực thuộc Nhà Máy:
    private ArrayList<Place> listPlace1 = new ArrayList<>();
    //Khai báo mảng Array List cho LisView của Thiết Bị thuộc Nhà Máy:
    private ArrayList<Device> listDevice1 = new ArrayList<>();
    //Khai báo mảng Adapter cho LisView của Khu Vực thuộc Nhà Máy:
    private ArrayAdapter<Place> placeAdapter;
    //Khai báo mảng Adapter cho LisView của Thiết Bị thuộc Nhà Máy:
    private ArrayAdapter<Device> deviceAdapter;

    //Tạo biến 'factoryName' để truyền:
    private String factoryName;
    private String factoryId;
    private String breadcrumbsText = "";
    Factory currentFactory;
    Place currentPlace;
    EditText editTextPlaceFact1;
    EditText editTextDeviceFact1;

    // Khai báo mã yêu cầu để nhận kết quả từ DeviceInfoActivity
    private static final int REQUEST_DEVICE_INFO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_factory);

        //Nhận dữ liệu "factory_name" từ FactoryActivity:
        factoryName = getIntent().getStringExtra("factory_name");
        factoryId = getIntent().getStringExtra("factory_id");
        breadcrumbsText = getIntent().getStringExtra("breadcrumbs_text");
        boolean isFromFactory = (getIntent().getStringExtra("from_factory") == "true");
        currentPlace = (Place) getIntent().getSerializableExtra("currentPlace");

        // Khởi tạo vào gán giá trị cho currentFactory
        currentFactory = new Factory();
        currentFactory.setName(factoryName);
        currentFactory.setId(factoryId);

        //Ánh xạ:
        editTextPlaceFact1 = findViewById(R.id.edtPlaceFact1);
        Button buttonAddPlace1 = findViewById(R.id.btnAddPlace1);
        //Khu vực:
        ListView listViewPlaceFact1 = findViewById(R.id.lvPlaceFact1);

        editTextDeviceFact1 = findViewById(R.id.edtDeviceFact1);
        Button buttonAddDevice1 = findViewById(R.id.btnAddDevice2);
        ListView listViewDeviceFact1 = findViewById(R.id.lvDeviceFact1);

        //Tạo 2 Mảng ArrayList mới cho Khu Vực & Thiết Bị
        listPlace1 = new ArrayList<>();
        listDevice1 = new ArrayList<>();

        //Tạo 2 đối tượng Array Adapter mới (Khu Vực & Thiết Bị):
        placeAdapter = new ArrayAdapter<>(this, R.layout.custom_list_item, listPlace1);
        deviceAdapter = new ArrayAdapter<>(this, R.layout.custom_list_item, listDevice1);

        //Thiết lập 2 Adapter đã tạo cho ListView:
        listViewPlaceFact1.setAdapter(placeAdapter);
        listViewDeviceFact1.setAdapter(deviceAdapter);


        // Kiểm tra xem ActionBar có được hỗ trợ không trước khi gán Name_Factory từ Factory:
         ActionBar actionBar = getSupportActionBar();
         if (actionBar != null) {
             //actionBar.setDisplayHomeAsUpEnabled(true);
             //Tiến hành các tác vụ tương ứng với mỗi item, ví dụ:
             //Hiển thị tên của item trên ActionBar hoặc tiêu đề của Activity
             actionBar.setTitle(currentFactory.getName());
        }

         if(!isFromFactory){
             LinearLayout breadcrumbsLayout = findViewById(R.id.breadcrumbs_layout);
             TextView breadcrumbItem = new TextView(this);
             breadcrumbItem.setText(breadcrumbsText);
             breadcrumbItem.setPadding(8, 8, 8, 8);

             breadcrumbsLayout.addView(breadcrumbItem);
         }

        //Tạo hành động cho nút "Thêm Khu Vực":
        buttonAddPlace1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lấy nội dung nhập từ EditText và biến thành kiểu String:
                String itemPlaceFact1 = editTextPlaceFact1.getText().toString();
                if (!itemPlaceFact1.isEmpty()) {
                    //Lệnh Nhập "Khu Vực" sẽ được Thực Thi:
                    boolean isPlaceExist = isExistPlaceName(itemPlaceFact1);
                    if (!isPlaceExist){
                        insertPlace(itemPlaceFact1);
                        editTextPlaceFact1.setText("");
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Tên khu vực đã tồn tại", Toast.LENGTH_SHORT).show();

                }
            }
        });
        //Tạo hành động cho nút "Thêm Thiết Bị":
        buttonAddDevice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lấy nội dung nhập từ EditText và biến thành kiểu String:
                String itemDeviceFact1 = editTextDeviceFact1.getText().toString();
                if (!itemDeviceFact1.isEmpty()) {
                    //Lệnh Nhập "Thiết Bị" sẽ được Thực Thi:

                    boolean isPlaceExist = isExistDeviceName(itemDeviceFact1);
                    if (!isPlaceExist){
                        insertDevice(itemDeviceFact1);
                        editTextDeviceFact1.setText("");
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Tên thiết bị đã tồn tại", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //Nhấn giữ Xóa Item trong ListView Khu Vực:
        listViewPlaceFact1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceFactory.this);
                builder.setMessage("Bạn có muốn xóa khu vực này không?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePlace(position);
                    }
                });
                builder.setNegativeButton("Không", null);
                builder.show();
                return true;
            }
        });
        //Nhấn giữ Xóa Item trong ListView Thiết Bị:
        listViewDeviceFact1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceFactory.this);
                builder.setMessage("Bạn có muốn xóa thiết bị này không?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDevice(position);
                    }
                });
                builder.setNegativeButton("Không", null);
                builder.show();
                return true;
            }
        });
        //Load Data Khu Vực & Thiết Bị:
        loadPlacesFromDatabase();

        // Nếu màn hình hiện tại là Factory thì load devices của Factory, ngược lại thì load của Area
        boolean isSelectByFactory = (currentFactory != null && currentPlace == null);
        loadDevicesFromDatabase(isSelectByFactory);

        //Khi nhấn vào mỗi item 'Thiết Bị' trong ListView  sẽ ra một màn hình DeviceInfo tương ứng với item 'Thiết Bị' đó :
        listViewDeviceFact1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Tạo biến item DeviceName (item Tên thiết bị) khi nhấn:
                Device device = listDevice1.get(i);
                //Chuyển sang màng hình DeviceInfo tương ứng:
                Intent intent = new Intent(PlaceFactory.this, DeviceInfo.class);
                //Truyền thiết bị qua Intent
                intent.putExtra("device", device); //Tên thiết bị
                intent.putExtra("factory_name", factoryName); //Khu vực
                intent.putExtra("factory_id", factoryId); // Factory_ID
                //Thực hiện việc chuyển đổi:
                startActivity(intent);
            }
        });
        //Khi nhấn vào mỗi area (khu vực) thuộc nhà máy đó sẽ cho ra khu vực & thiết bị thuộc nhà máy đó:
        listViewPlaceFact1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Place place = listPlace1.get(i);
                Intent intent = new Intent(PlaceFactory.this, PlaceFactory.class);
                //Truyền dữ liệu factory_name qua "PlaceFactoryActivity":

                if(breadcrumbsText == null){
                    breadcrumbsText = currentFactory.getName();
                    breadcrumbsText += "/" + place.getName();
                }else {

                    if(!breadcrumbsText.contains("/" + place.getName()))
                        breadcrumbsText += "/" + place.getName();
                }

                intent.putExtra("factory_name", currentFactory.getName());
                intent.putExtra("factory_id", currentFactory.getId());
                intent.putExtra("area_name", place.getName());
                intent.putExtra("from_factory", "false");
                intent.putExtra("breadcrumbs_text", breadcrumbsText);
                intent.putExtra("currentPlace", place);
                startActivity(intent);
            }
        });
    }

    //Lệnh Nhập "Khu Vực"
    private void insertPlace(String placeName) {
        connection = connectionToDatabase(ConnectToDatabase.username.toString(),ConnectToDatabase.password.toString(),ConnectToDatabase.db.toString(),ConnectToDatabase.ip.toString());
        if (connection != null) {
            try {

                boolean isForFactoryInsert = (currentFactory != null && currentPlace == null);
                String query = "";
                PreparedStatement preparedStatement;

                if(isForFactoryInsert){
                    query = "INSERT INTO AREA (NAME, ID_FACTORY) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, placeName);
                    preparedStatement.setString(2, currentFactory.getId());
                }else {
                    query = "INSERT INTO AREA (NAME, ID_AREA) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, placeName);
                    preparedStatement.setString(2, currentPlace.getId());
                }


                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    loadPlacesFromDatabase();
                    placeAdapter.notifyDataSetChanged();
                    editTextPlaceFact1.setText("");
                    Toast.makeText(getApplicationContext(), "Nhập thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Nhập không thành công", Toast.LENGTH_SHORT).show();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Lỗi khi thêm vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kết nối không thành công", Toast.LENGTH_SHORT).show();
        }
    }
    //Lệnh Nhập "Thiết Bị":
    private void insertDevice(String deviceName) {
        listDevice1.clear();
        connection = connectionToDatabase(ConnectToDatabase.username.toString(),ConnectToDatabase.password.toString(),ConnectToDatabase.db.toString(),ConnectToDatabase.ip.toString());
        if (connection != null) {
            try {
                /*
                 * Nhập Tên Thiết Bị (DEVICE_NAME) lấy theo Tên Nhà Máy (NAME_FACTORY):
                 * */
                String query = "INSERT INTO DEVICE " +
                        "(NAME, ID_FACTORY, ID_AREA, MANAGE_CODE, INSTALLATION_DAY, LOCATION, INSPECTOR, MAINTENANCE_PERSON, DESCRIPTION, IMAGE_UPLOAD) " +
                        "VALUES (?, ?, ?, ? ,? ,? , ?, ? ,?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, deviceName);

                // Kiểm tra xem màn hình này đang là Factory hay Place
                if(currentPlace != null){
                    preparedStatement.setString(2, "");
                    preparedStatement.setString(3, currentPlace.getId());
                }else {
                    preparedStatement.setString(2, currentFactory.getId());
                    preparedStatement.setString(3, "");
                }

                preparedStatement.setString(4, "");
                preparedStatement.setString(5, "");
                preparedStatement.setString(6, "");
                preparedStatement.setString(7, "");
                preparedStatement.setString(8, "");
                preparedStatement.setString(9, "");
                preparedStatement.setString(10, "");



                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    boolean isSelectByFactory = (currentFactory != null && currentPlace == null);
                    loadDevicesFromDatabase(isSelectByFactory);
                    Toast.makeText(getApplicationContext(), "Nhập thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Nhập không thành công", Toast.LENGTH_SHORT).show();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Lỗi khi thêm vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kết nối không thành công", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePlace(int position) {
        Place place = listPlace1.get(position);
        connection = connectionToDatabase(ConnectToDatabase.username.toString(),ConnectToDatabase.password.toString(),ConnectToDatabase.db.toString(),ConnectToDatabase.ip.toString());
        if (connection != null) {
            try {
                String query = "DELETE FROM AREA WHERE ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, place.getId());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    loadPlacesFromDatabase();
                    Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Xóa không thành công", Toast.LENGTH_SHORT).show();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Lỗi khi xóa từ cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kết nối không thành công", Toast.LENGTH_SHORT).show();
        }
    }

    //Xóa itemThiết bị:
    private void deleteDevice(int position) {
        Device device = listDevice1.get(position);
        connection = connectionToDatabase(ConnectToDatabase.username.toString(),ConnectToDatabase.password.toString(),ConnectToDatabase.db.toString(),ConnectToDatabase.ip.toString());
        if (connection != null) {
            try {
                String query = "DELETE FROM DEVICE WHERE ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, device.getId());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    boolean isForFactoryInsert = (currentFactory != null && currentPlace == null);
                    loadDevicesFromDatabase(isForFactoryInsert);
                    deviceAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Xóa không thành công", Toast.LENGTH_SHORT).show();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Lỗi khi xóa từ cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kết nối không thành công", Toast.LENGTH_SHORT).show();
        }
    }
    //Xóa thông tin DeviceInfo theo DeviceName
//    private void deleteDeviceInfo(int position) {
//        String deviceName = listDevice1.get(position);
//        connection = connectionToDatabase(ConnectToDatabase.username.toString(),ConnectToDatabase.password.toString(),ConnectToDatabase.db.toString(),ConnectToDatabase.ip.toString());
//        if (connection != null) {
//            try {
//                String query = "DELETE FROM DEVICE WHERE ID = ?";
//                PreparedStatement preparedStatement = connection.prepareStatement(query);
//                preparedStatement.setString(1, deviceName);
//                int rowsAffected = preparedStatement.executeUpdate();
//                if (rowsAffected > 0) {
//                    listDevice1.remove(position);
//                    deviceAdapter.notifyDataSetChanged();
//                    Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Xóa không thành công", Toast.LENGTH_SHORT).show();
//                }
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Lỗi khi xóa từ cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "Kết nối không thành công", Toast.LENGTH_SHORT).show();
//        }
//    }
    //Load Data:
    private void loadPlacesFromDatabase() {
        connection = connectionToDatabase(ConnectToDatabase.username.toString(),ConnectToDatabase.password.toString(),ConnectToDatabase.db.toString(),ConnectToDatabase.ip.toString());
        if (connection != null) {
            try {
                String query = "";
                PreparedStatement preparedStatement;

                boolean isSelectByFactory = (currentFactory != null && currentPlace == null);

                if(isSelectByFactory){
                    query = "SELECT * FROM AREA WHERE ID_FACTORY = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, currentFactory.getId());
                }else {
                    query = "SELECT * FROM AREA WHERE ID_AREA = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, currentPlace.getId());
                }

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {

                    String id = resultSet.getString("ID");
                    String idArea = resultSet.getString("ID_AREA");
                    String idFactory = resultSet.getString("ID_FACTORY");
                    String placeName = resultSet.getString("NAME");

                    Place place = new Place();
                    place.setId(id);
                    place.setIdPlace(idArea);
                    place.setIdFactory(idFactory);
                    place.setName(placeName);
                    listPlace1.add(place);
                }
                connection.close();
                placeAdapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Lỗi khi tải dữ liệu từ cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kết nối không thành công", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDevicesFromDatabase(boolean isSelectByFactory) {
        listDevice1.clear();
        //Tạo kết nối:
        connection = connectionToDatabase(ConnectToDatabase.username.toString(),ConnectToDatabase.password.toString(),ConnectToDatabase.db.toString(),ConnectToDatabase.ip.toString());
        if (connection != null) {
            try {
                String query = "";
                PreparedStatement preparedStatement;
                if(isSelectByFactory){
                    query = "SELECT * FROM DEVICE WHERE ID_FACTORY = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, currentFactory.getId());
                }
                else{
                    query = "SELECT * FROM DEVICE WHERE ID_AREA = ? ";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, currentPlace.getId());
                }

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {

                    String id = resultSet.getString("ID");
                    String idFactory = resultSet.getString("ID_FACTORY");
                    String idArea = resultSet.getString("ID_AREA");
                    String name = resultSet.getString("NAME");
                    String manageCode = resultSet.getString("MANAGE_CODE");
                    String installationDay = resultSet.getString("INSTALLATION_DAY");
                    String location = resultSet.getString("LOCATION");
                    String inspector = resultSet.getString("INSPECTOR");
                    String maintenancePerson = resultSet.getString("MAINTENANCE_PERSON");
                    String description = resultSet.getString("DESCRIPTION");
                    String imageUpload = resultSet.getString("IMAGE_UPLOAD");

                    Device device = new Device();
                    device.setId(id);
                    device.setIdFactory(idFactory);
                    device.setIdArea(idArea);
                    device.setName(name);

                    device.setManageCode(String.valueOf(manageCode));
                    device.setLocation(location);
                    device.setInstallationDay(installationDay);
                    device.setInspector(inspector);
                    device.setMaintenancePerson(maintenancePerson);
                    device.setDescription(description);
                    device.setImgUpload(imageUpload);

                    listDevice1.add(device);
                }
                connection.close();
                deviceAdapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Lỗi khi tải dữ liệu từ cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kết nối không thành công", Toast.LENGTH_SHORT).show();
        }
    }
    //Kết nối đến CSDL SQL Server:
    @SuppressLint("NewApi")
    public Connection connectionToDatabase(String user, String password, String database, String server){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + server+"/" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionURL);
        }catch (Exception e){
            Log.e("SQL Connection Error : ", e.getMessage());
        }
        return connection;
    }

    // Hàm kiểm tra tên nhà máy đã có thì không cho thêm
    private boolean isExistDeviceName(String nameToCheck){
        if (listDevice1.isEmpty())
            return false;

        for(int i = 0; i < listDevice1.size(); i++){
            if(Objects.equals(nameToCheck, listDevice1.get(i).getName()))
                return true;
        }
        return false;
    }

    private boolean isExistPlaceName(String nameToCheck){
        if (listPlace1.isEmpty())
            return false;

        for(int i = 0; i < listPlace1.size(); i++){
            if(Objects.equals(nameToCheck, listPlace1.get(i).getName()))
                return true;
        }
        return false;
    }
}
