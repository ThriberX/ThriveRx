package com.thriber.thriverx




import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerValue
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.thriber.thriverx.FirebaseClass.DataInterface.DataInterface
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.thriber.thriverx.user_creation.IntroActivity
import com.thriber.thriverx.user_creation.bounceClick
import com.thriber.thriverx.user_creation.ui.theme.bg_colour_patient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID


data class PatientItem(      val id:String="" , // unique id for every patient which is formed using uuid
                             var Name:String="", //name of patient
                            var MiddleName:String="",
                            var LastName:String="",
                             var Age:String="",
                             var Gender:String="",
                             var Address:String="",
                            var city:String="",
                            var pincode:String="",
                            var state:String="",
                            val phoneNumber:String="",
                            val formatiodate:String="",// date on which the patient account was added
                           )



    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    fun PatientList( onTakePictureClick: (String) -> Unit) {
        var sItems by remember { mutableStateOf(listOf<PatientItem>()) }  //all the detail of patient in which they are added when patient data is being displayed on UI
        var showDialog by remember { mutableStateOf(false) } // used for the state of dialog box used to add the patient
        var logoutDialog by remember { mutableStateOf(false) }//  used for the state of dialog box used to logout the hospital account
        var itemName by remember { mutableStateOf("") }  // patient name first filled in form
        var itemMiddleName by remember { mutableStateOf("") } // patient middle name filled in form
        var itemLastName by remember { mutableStateOf("") }  // patient last name filled in form
        var itemAge by remember { mutableStateOf("") }    //patient age filled in form
        var itemGender by remember { mutableStateOf("") } //patient gender filled in form
        var itemaddress by remember { mutableStateOf("") } // patient initial address filled in  where they live
        var itemCity by remember { mutableStateOf("") }  // patient city filled in form
        var itemPincode by remember { mutableStateOf("") } // patient pin code filled in form
        var itemState by remember { mutableStateOf("") }   // patient residential state where they live
        var itemPhonenumber by remember { mutableStateOf("") } // patient phone number
        var text by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(false) }
        var datepicker by remember { mutableStateOf(false) }
        var initialDateString by remember { mutableStateOf("") }
        var finalDateString by remember { mutableStateOf("") }
        var copiedlist by remember { mutableStateOf(listOf<PatientItem>()) } // used to remember  original list of patient so that if any filter of date or search is applied original list does not get harm
        val genderOptions = listOf("M", "F")  // gender options shown in the form while selecting gender of patient
        var expanded by remember { mutableStateOf(false) }
        var drawerState = rememberDrawerState(DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email ?: "No User Logged In"
        var errorMessage by remember { mutableStateOf<String?>(null) }

         val fetchPatientcredentials:DataInterface=FirebaseDao()


        fun fetchPatients(text:String) {
            errorMessage = null
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = fetchPatientcredentials.fetchPatient(text)
                    withContext(Dispatchers.Main) {
                        sItems = result
                        copiedlist=result
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage = e.message
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            fetchPatients(text)
        }



        fun Context.getActivity(): AppCompatActivity? = when (this) {
            is AppCompatActivity -> this
            is ContextWrapper -> baseContext.getActivity()
            else -> null
        }

        val localContext = LocalContext.current.getActivity()


        @Composable
        fun dateRangePickerScreen() {

            if (datepicker == true) {
                AlertDialog(onDismissRequest = { datepicker = false }) {
                    val dateRangePickerState = remember {
                        DateRangePickerState(
                            initialSelectedStartDateMillis = null,
                            initialSelectedEndDateMillis = null,
                            initialDisplayedMonthMillis = null,
                            yearRange = (2024..2025),
                            initialDisplayMode = DisplayMode.Input,


                            )
                    }
                    DateRangePicker(
                        state = dateRangePickerState,

                        title = {
                            Text(
                                text = "Record formation date",
                                modifier = Modifier.padding(12.dp)
                            )
                        },
                        headline = {
                            Text(
                                text = "Start-Date - End-Date",
                                style = (TextStyle(fontSize = 18.sp, color = Color.Black)),
                                modifier = Modifier.padding(8.dp)
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color.White)
                            .border(
                                border = BorderStroke(2.dp, Color.White),
                                shape = RoundedCornerShape(5)
                            ),

                        )
                    val initialDateMillis = dateRangePickerState.selectedStartDateMillis
                    val finalDateMillis = dateRangePickerState.selectedEndDateMillis




                    if (initialDateMillis != null && finalDateMillis != null) {
                        datepicker = false

                        val initialDate = Date(initialDateMillis)
                        val finalDate = Date(finalDateMillis)


                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        initialDateString = dateFormat.format(initialDate)
                        finalDateString = dateFormat.format(finalDate)

                        sItems = sItems.filter { patient ->
                            val patientFormationDate = patient.formatiodate
                            patientFormationDate >= initialDateString && patientFormationDate <= finalDateString
                        }


                    }
                }
            }


        }


        fun clearDate() {

            initialDateString = ""
            finalDateString = ""
            sItems = copiedlist
        }


        @Composable
        fun dateSelectorButton() {

            Column(
                verticalArrangement = Arrangement.Center
            ) {


                Button(
                    onClick = { datepicker = true },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.bounceClick()
                        .padding(10.dp),
                )
                {

                    dateRangePickerScreen()
                    if (initialDateString != "" && finalDateString != "") {
                        Text(
                            text = "$initialDateString to $finalDateString",
                            style = TextStyle(color = Color.Gray)
                        )
                        IconButton(onClick = {
                            clearDate()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                tint = Color.Black,
                                contentDescription = "Clear dates"

                            )
                        }
                    } else {
                        Text(
                            text = "SELECT DATE",
                            style = TextStyle(color = Color.Gray),
                            modifier = Modifier.padding(3.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            tint = Color.Black,
                            contentDescription = "Clear dates",
                        )

                    }

                }

            }
        }


        /**
         * Column composable to display UI elements vertically.
         * @param text The search text used to filter patient items.
         * @param active Flag indicating whether the search bar is active.
         * @param fetchPatients Callback function to fetch patients based on the search text.
         * @param showDialog Function to toggle the visibility of the add patient dialog.
         */


        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                // Drawer content
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(top = 32.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout Button
                    Button(
                        onClick = {
                            logoutDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5555)),
                        modifier = Modifier.bounceClick()
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Log Out", color = Color.White)
                    }
                }
            }
        ) {

            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {

                        coroutineScope.launch {
                            // Toggle drawer state
                            if (drawerState.isOpen) {
                                drawerState.close() // Close the drawer if open
                            } else {
                                drawerState.open() // Open the drawer if close
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Hamburger Menu",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "RECORDS",
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .weight(1f),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Dialogbox to confirm logout
                if (logoutDialog) {
                    AlertDialog(
                        onDismissRequest = { logoutDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.thriverlogo),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp)
                                )

                                Text(
                                    text = "Logout",
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Do you want to logout?",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )

                                Button(
                                    onClick = {
                                        FirebaseDao().sigout()
                                        localContext?.startActivity(
                                            Intent(
                                                localContext,
                                                IntroActivity::class.java
                                            )
                                        )
                                        localContext?.finish()
                                    },
                                    modifier = Modifier.bounceClick()
                                        .fillMaxWidth(0.8f)
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFFFF5555
                                        )
                                    )
                                ) {
                                    Text("Yes, Logout", fontSize = 16.sp, color = Color.White)
                                }

                                Button(
                                    onClick = { logoutDialog = false },
                                    modifier = Modifier.bounceClick()
                                        .fillMaxWidth(0.8f)
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text("Cancel", fontSize = 16.sp, color = Color.Black)
                                }
                            }
                        })
                }

                // SearchBar composable to input and search for patient
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bg_colour_patient) // Set the background color of the row to blue
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.55f)
                            .height(70.dp)
                    ) {
                        SearchBar(
                            query = text,
                            onQueryChange = { newText -> text = newText },
                            onSearch = { active = false },
                            active = active,
                            onActiveChange = { active = it },
                            placeholder = { Text(text = "Search patient") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon"
                                )
                            },
                            trailingIcon = {
                                // Close icon to clear search text or deactivate search
                                if (active) {
                                    Icon(
                                        modifier = Modifier.clickable {
                                            if (text.isNotEmpty()) {
                                                text = ""
                                            } else {
                                                active = false
                                            }
                                        },
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Icon"
                                    )
                                }
                            }
                        ) {
                            // Fetch patients when search text is not empty
                            if (text.isNotEmpty()) {
                                fetchPatients(text)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().currentUser?.reload()
                            showDialog = true   },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), 
                        modifier = Modifier.bounceClick()
                            .padding(8.dp)
                            .weight(0.30f)
                            .graphicsLayer {
                            shadowElevation = 8.dp.toPx()
                            shape = RoundedCornerShape(15.dp)
                            clip = true
                        }

                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFA9F700), // Gradient start color
                                        Color(0xFF8EBB00)  // Gradient end color
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            ),
                    ) {
                        Text(text = "Add Patient", color = Color.Black , fontWeight =FontWeight.Bold)
                    }
                }

                // Row to display the date selector button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    dateSelectorButton()
                }

                // Box to display a LazyColumn of patient items
                Box(modifier = Modifier.fillMaxSize()) {
                    val state = rememberLazyListState()
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        state = state
                    ) {
                        // Filter the items based on search text
                        val filteredItems = sItems.filter {
                            val fullName = "${it.Name} ${it.MiddleName} ${it.LastName}"
                                .replace("\\s".toRegex(), "")
                            val searchText = text.replace("\\s".toRegex(), "")
                            fullName.contains(searchText, ignoreCase = true)
                        }

                        // Display each filtered item as a patient list item
                        items(filteredItems) {
                            patientListItem(
                                it,
                                onTakePictureClick = { itemId -> onTakePictureClick(itemId) })
                        }
                    }
                }
            }
        }

        /**
         * Composable function to display UI elements for managing patients.
         * @param active Flag indicating whether the search bar is active.
         * @param showDialog Function to toggle the visibility of the add patient dialog.
         */
        if (showDialog && FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
            // Display an AlertDialog if showDialog flag is true

            AlertDialog(containerColor = Color(0xFF0F192E),
                onDismissRequest = { showDialog = false },

                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier
                    .fillMaxHeight(),

                confirmButton = {
                    // Row to hold the confirm and cancel buttons
                    Row(
                        modifier = Modifier

                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Button( // Confirm button to add a new patient
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB5F6FF), // Background color of the button
                                contentColor = Color.Black // Text color of the button
                            ),
                            onClick = {
                                // Check if all required fields are filled
                                if (
                                    itemAge.isNotEmpty() &&
                                    itemName.isNotEmpty() &&
                                    itemaddress.isNotEmpty() &&
                                    itemPhonenumber.isNotEmpty()
                                ) {
                                    // Create a new PatientItem
                                    val newitem = PatientItem(
                                        id = UUID.randomUUID().toString(),
                                        Address = itemaddress,
                                        city = itemCity,
                                        pincode = itemPincode,
                                        state = itemState,
                                        Gender = itemGender,
                                        Name = itemName,
                                        MiddleName = itemMiddleName,
                                        LastName = itemLastName,
                                        Age = itemAge,
                                        phoneNumber = itemPhonenumber,
                                        formatiodate = LocalDate.now()
                                            .format(DateTimeFormatter.ISO_DATE)
                                    )
                                    // Add the new patient to the list
                                    sItems = sItems + newitem
                                    // Calculate the new patient ID
                                    val newPatientId = newitem.id
                                    // Upload the new patient to Firestore
                                    uploadPatientCredentials(newitem, newPatientId)

                                    // Hide the dialog
                                    showDialog = false
                                }


                            },
                            modifier = Modifier.bounceClick().padding(16.dp)
                        )
                        {
                            Text("Add")
                        }
                        Image(
                            painter = painterResource(id = R.drawable.thriverlogo),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp) // Set a size for the image (optional)
                        )

                        Button(colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black// Text color of the button
                        ), onClick = { showDialog = false }, modifier = Modifier.bounceClick().padding(16.dp)
                        )


                        {
                            itemName = ""
                            itemAge = ""
                            itemCity = ""
                            itemPhonenumber = ""
                            itemLastName = ""
                            itemMiddleName = ""
                            itemState = ""
                            itemaddress = ""
                            itemPincode = ""
                            itemGender = ""
                            Text("Cancel")

                        }

                    }
                },

                title = { Text(text = "Add Patient Details", color = Color.White) },
                text = {


                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {


                        OutlinedTextField(
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),

                            value = itemName,
                            onValueChange = { newName ->

                                if (newName.isEmpty() || (newName.length <= 50 && newName.matches(
                                        "[a-zA-Z]*".toRegex()
                                    ))
                                ) {

                                    val nameSplit=newName.trim().split("\\s+".toRegex())

                                    when(nameSplit.size){
                                        1->{
                                            itemName= nameSplit[0]
                                            itemMiddleName = ""
                                            itemLastName = ""
                                        }
                                        2->{
                                            itemName= nameSplit[0]
                                            itemMiddleName = ""
                                            itemLastName = nameSplit[1]
                                        }
                                        else ->{

                                            itemName = nameSplit[0]
                                            itemMiddleName = nameSplit.last()

                                            itemLastName = nameSplit.subList(1, nameSplit.size - 1)
                                                .joinToString(" ")

                                        }
                                    }

                                }

                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),

                            placeholder = { Text("Name") },
                            isError = itemName.length <= 0,

                            )


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            OutlinedTextField(
                                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                                value = itemAge,
                                onValueChange = { newAge ->
                                    if (newAge.isEmpty() || (newAge.length <= 3 && newAge.matches(
                                            "-?[0-9]+(\\.[0-9]+)?".toRegex()
                                        ))
                                    ) {
                                        itemAge = newAge
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp),
                                placeholder = { Text("Age") },
                                isError = itemAge.length <= 0
                            )

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                                    readOnly = true,
                                    value = itemGender,
                                    onValueChange = { },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp),
                                    placeholder = { Text("Gender") },
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    genderOptions.forEach { gender ->
                                        DropdownMenuItem(onClick = {
                                            itemGender = gender
                                            expanded = false
                                        }) {
                                            Text(gender)
                                        }
                                    }
                                }
                            }
                        }


                        OutlinedTextField(
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                            value = itemaddress, onValueChange = { newAdress ->
                                if (newAdress.isEmpty() || (newAdress.isNotEmpty() && newAdress.length < 200))
                                    itemaddress = newAdress
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),

                            placeholder = { Text("Address") },
                            isError = itemaddress.length <= 0
                        )


                        OutlinedTextField(
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                            value = itemPincode,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            onValueChange = { newPincode ->
                                if (newPincode.isEmpty() || (newPincode.length <= 6 && newPincode.matches(
                                        "-?[0-9]+(\\.[0-9]+)?".toRegex()
                                    ))
                                )
                                    itemPincode = newPincode
                                    errorMessage=null

                                if (itemPincode.length == 6) {
                                    fetchPincodeData(itemPincode) { fetchcity, fetchstate ->
                                        if(fetchcity.isEmpty()|| fetchstate.isEmpty()){
                                        itemCity = fetchcity
                                        itemState = fetchstate
                                            errorMessage="Invalid pincode"
                                            } else{
                                            errorMessage = null
                                            itemCity = fetchcity
                                            itemState = fetchstate
                                        }


                                    }
                                }


                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),


                            placeholder = {
                                Text(
                                    "pincode",
                                    style = TextStyle(fontSize = 12.sp)
                                )
                            },
                            isError = errorMessage != null,
                            supportingText = {
                                errorMessage?.let {
                                    Text(
                                        it,
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            }

                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),

                            ) {
                            OutlinedTextField(
                                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                                value = itemCity,
                                onValueChange = { newCity ->
                                    if (newCity.isEmpty() || (newCity.length <= 50 && newCity.matches(
                                            "-?[a-zA-Z]+(\\.[a-zA-Z]+)?".toRegex()
                                        ))
                                    )
                                        itemCity = newCity
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp),
                                placeholder = {
                                    Text("City", style = TextStyle(fontSize = 12.sp))
                                },

                                )
                            OutlinedTextField(
                                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                                value = itemState, onValueChange = { newState ->

                                    if (newState.isEmpty() || (newState.length <= 35 && newState.matches(
                                            "-?[a-zA-Z]+(\\s[a-zA-Z]+)*\\s*".toRegex()
                                        ))
                                    ) {
                                        itemState = newState
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp),
                                placeholder = {
                                    Text("State", style = TextStyle(fontSize = 12.sp))
                                },
                                isError = itemState.length > 35 || itemState.any { it.isDigit() },
                                textStyle = TextStyle(color = if (itemState.length > 30 || itemState.any { it.isDigit() }) Color.Red else Color.Black)

                            )

                        }




                        OutlinedTextField(
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                            value = itemPhonenumber,
                            onValueChange = { newNumber ->
                                if (newNumber.isEmpty() || (newNumber.length <= 10 && newNumber.matches(
                                        "-?[0-9]+(\\.[0-9]+)?".toRegex()
                                    ))
                                ) {
                                    itemPhonenumber = newNumber
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            placeholder = { Text("Phone number") },
                            isError = itemPhonenumber.length <= 9,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

                        )
                    }

                })

        } else{
            if (showDialog && FirebaseAuth.getInstance().currentUser?.isEmailVerified==false) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    confirmButton = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.thriverlogo),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )

                            Text(
                                text = "Email verification",
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Check email for verification mail. If on email please click button",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )

                            Button(
                                onClick = {
                                    val user = Firebase.auth.currentUser
                                    if (user != null && !user.isEmailVerified) {
                                        user.sendEmailVerification()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        localContext,
                                                        "Verification email sent!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        localContext,
                                                        "Failed to send email. Try again.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    }

                                },
                                modifier = Modifier.bounceClick()
                                    .fillMaxWidth(0.8f)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(8.dp),
                                elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFF5555
                                    )
                                )
                            ) {
                                Text("Verification email", fontSize = 16.sp, color = Color.White)
                            }

                            Button(
                                onClick = { showDialog = false },
                                modifier = Modifier.bounceClick()
                                    .fillMaxWidth(0.8f)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(8.dp),
                                elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("Cancel", fontSize = 16.sp, color = Color.Black)
                            }
                        }
                    })
            }
        }
    }


        /**
         * Composable function for displaying a single patient item in a list.
         * @param item The patient item to display.
         * @param onTakePictureClick Callback function to handle the click event for viewing documents.
         */

        @Composable
        fun patientListItem(
            item: PatientItem,
            onTakePictureClick: (String) -> Unit
         )
        {
            // Column composable to arrange UI elements vertically

            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF0F192E))
                    .clickable { onTakePictureClick(item.id) }
            ) {
                Row(
                    modifier = Modifier.padding(8.dp) // Adding padding for better spacing
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.group_),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp) // Set a size for the image (optional)
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Space between image and text

                    Column {
                        Row {
                        // Display patient's name
                        Text(
                            text = "${item.Name} ${item.MiddleName} ${item.LastName} ",
                            style = TextStyle(color = Color.White),
                            modifier = Modifier.padding(bottom = 4.dp) // Add bottom padding for spacing
                        )

                        // Display patient's age and gender in the same row

                            Text(
                                text = "| ${item.Age} Years",
                                style = TextStyle(color = Color.Gray),
                                modifier = Modifier.padding(end = 4.dp)
                            )

                            // Display patient's gender
                            Text(
                                text = "| ${item.Gender}",
                                style = TextStyle(color = Color.Gray)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp)) // Space between name/age and phone number

                        // Display patient's phone number
                        Text(
                            text = "${item.phoneNumber}",
                            style = TextStyle(color = Color.White)
                        )
                    }
                }
                Divider(
                    color = Color.White, // Set the divider color to white
                    thickness = 2.dp,    // Adjust the thickness of the divider (optional)
                    modifier = Modifier.padding(top = 8.dp) // Optional top padding for spacing
                )
            }


        }


        /**
         * Uploads a patient to Firebase Firestore and creates a corresponding folder in Firebase Storage.
         * @param patient The patient information to be uploaded.
         * @param patientId The ID of the patient.
         */
   private val credentials:DataInterface=FirebaseDao()
fun uploadPatientCredentials(PatientCredential:PatientItem, patientId:String){

        credentials.uploadPatientCredentials(PatientCredential, patientId)
}



private fun fetchPincodeData(pincode: String, callback: (String, String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = withContext(Dispatchers.IO) {
                java.net.URL("https://api.postalpincode.in/pincode/$pincode").readText()
            }

            val jsonArray = JSONArray(response)
            val jsonObject = jsonArray.getJSONObject(0)

            if (jsonObject.getString("Status") == "Error") {
                withContext(Dispatchers.Main) {
                    callback("", "")
                }

            }

            val postOfficeArray = jsonObject.getJSONArray("PostOffice")
            if (postOfficeArray.length() > 0) {
                val firstPostOffice = postOfficeArray.getJSONObject(0)
                val city = firstPostOffice.getString("District")
                val state = firstPostOffice.getString("State")

                withContext(Dispatchers.Main) {
                    callback(city, state)
                }
            } else {
                withContext(Dispatchers.Main) {
                    callback("", "")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callback("", "")
            }
        }
    }
}
