
public class PhotoUploadActivity extends AppCompatActivity {

    ActivityPhotoUploadBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userName;

  private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MainTheme.SettingThemes(this);


        binding.backImg.setOnClickListener(v -> {
            onBackPressed();
        });
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userName = sharedPreferences.getString("userName","");
        String userEmail = sharedPreferences.getString("userEmail","");



        binding.tvName.setText("@"+userName);


        // Upload image
        binding.selectImageId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                galaryLauncer.launch(intent);
            }
        });



        binding.uploadButton.setOnClickListener(v -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.tvImage.getDrawable();
            Bitmap bitmap1 = bitmapDrawable.getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            byte [] imageByte =
                    byteArrayOutputStream.toByteArray();
            String imageToString = Base64.encodeToString(imageByte,Base64.DEFAULT);

            if(!userName.isEmpty() && !imageToString.isEmpty()){

                uploadImage(userName, imageToString,"https://exmaple.com/uploadPhoto.php");
            }

        });


    }// OnCreate end here

    private void uploadImage(String userName, String imageToString, String url) {
        binding.text.setVisibility(GONE);
        binding.progressbarId.setVisibility(VISIBLE);


        // Import Volley library
        RequestQueue requestQueue = Volley.newRequestQueue(PhotoUploadActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                binding.text.setVisibility(VISIBLE);
                binding.progressbarId.setVisibility(GONE);

                if(response.contains("Successfully updated")){
                  //  Toast.makeText(PhotoUploadActivity.this, "Successfully upload", Toast.LENGTH_SHORT).show();
                    editor.remove("imageUrl");
                    editor.apply();
                    startActivity(new Intent(PhotoUploadActivity.this, MainActivity.class));
                    finish();

                }else{
                    Toast.makeText(PhotoUploadActivity.this, "please try gain", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.text.setVisibility(VISIBLE);
                binding.progressbarId.setVisibility(GONE);
                Toast.makeText(PhotoUploadActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("userName",userName);
                getParams.put("userImage",imageToString);
                return getParams;
            }
        };


        requestQueue.add(stringRequest);
    }


    ActivityResultLauncher<Intent> galaryLauncer = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK){

                Intent intent = result.getData();

                Uri FilePath = intent.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),FilePath);

                    binding.tvImage.setImageBitmap(bitmap);
                    binding.tvMessageId.setText("✔ Choose a New Photo");

                } catch (IOException e) {
                    throw new RuntimeException(e); }
            }
        }
    });// ActivityResultLauncher end here ===================================
}