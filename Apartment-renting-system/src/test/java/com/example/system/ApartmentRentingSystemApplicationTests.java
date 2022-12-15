package com.example.system;

import com.example.system.Entities.SavedList;
import com.example.system.Entities.User;
import com.example.system.Repositories.*;
import com.example.system.Services.UserService;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApartmentRentingSystemApplicationTests {

	@Autowired
	UserService userService;
	@Autowired
	MockMvc mockMvc;

	@Autowired
	UserRepo userRepo;
	@Autowired
	AdRepo adRepo;
	@Autowired
	HouseTypeRepo houseTypeRepo;
	@Autowired
	PhotoRepo photoRepo;
	@Autowired
	PromotionExpirationRepo promotionExpirationRepo;
	@Autowired
	PromotionTypeRepo promotionTypeRepo;
	@Autowired
	RoleRepo roleRepo;
	@Autowired
	ViewersRepo viewersRepo;
	@Autowired
	SavedListRepo savedListRepo;

	private final String username = "art";
	private final String role = "USER";

	// authentication
	@Test
	void register() throws Exception {


		// validation error
		mockMvc.perform(multipart("/auth/register").file("image", null)
				.param("data", "{\n" +
						"    \"fullName\": \"\",\n" +
						"     \"email\":\"\",\n" +
						"    \"username\":\"test\",\n" +
						"    \"password\":\"\",\n" +
						"    \"contactInfo\": \"\"\n" +
						"}"))
				.andDo(print())
				.andExpect(status().isBadRequest());


		if(userRepo.findByUsername(username) == null){
			mockMvc.perform(multipart("/auth/register").file("image", null)
							.param("data", "{\n" +
									"    \"fullName\": \"test user\",\n" +
									"     \"email\":\"test@gmail.com\",\n" +
									"    \"username\":\"" + username  +"\",\n" +
									"    \"password\":\"1234\",\n" +
									"    \"contactInfo\": \"phone number: 909 505 505\"\n" +
									"}"))
					.andDo(print())
					.andExpect(status().isCreated());
		}
		mockMvc.perform(multipart("/auth/register").file("image", null)
						.param("data", "{\n" +
								"    \"fullName\": \"test user\",\n" +
								"     \"email\":\"test@gmail.com\",\n" +
								"    \"username\":\"" +  username + "\",\n" +
								"    \"password\":\"1234\",\n" +
								"    \"contactInfo\": \"phone number: 909 505 505\"\n" +
								"}"))
				.andDo(print())
				.andExpect(status().isBadRequest());

	}

	@Test
	void login() throws Exception {

		String[] usernames = new String[]{username, RandomString.make(10)};
		String password = "1234";

		for(String username : usernames) {

			if (userRepo.findByUsername(username) != null) {
				if(userRepo.findByUsername(username).isEnabled()) {
					mockMvc.perform(post("/auth/login").content("{\n" +
									"    \"username\": \"" + username + "\",\n" +
									"    \"password\": \"" + password + "\"\n" +
									"}").contentType("application/json"))
							.andDo(print())
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.token").exists());
				}else {
					mockMvc.perform(post("/auth/login").content("{\n" +
									"    \"username\": \"" + username + "\",\n" +
									"    \"password\": \"" + password + "\"\n" +
									"}").contentType("application/json"))
							.andDo(print())
							.andExpect(status().isForbidden())
							.andExpect(jsonPath("$.token").doesNotExist());
				}

			}else {
				mockMvc.perform(post("/auth/login").content("{\n" +
								"    \"username\": \"" + username + "\",\n" +
								"    \"password\": \"" + password + "\"\n" +
								"}").contentType("application/json"))
						.andDo(print())
						.andExpect(status().isUnauthorized())
						.andExpect(jsonPath("$.token").doesNotExist());
			}
		}


	}


	// profile

	@Test
	@WithMockUser(username = username, roles = role)
	void getProfile() throws Exception {

		mockMvc.perform(get("/user/profile"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(userRepo.findByUsername(username).getId()));
	}

	@Test
	@WithMockUser(username = username, roles = role)
	void editProfile() throws Exception {
		mockMvc.perform(multipart("/user/profile").file("image", null).with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setMethod("PUT");
						return request;
					}
				}).param("data", "{\"fullName\": \"New Name\", \"contactInfo\": \"telegram: @new\"\n" +
						"}"))
				.andDo(print())
				.andExpect(status().isNoContent());
		Assert.assertEquals("New Name", userRepo.findByUsername(username).getFullName());

	}

	@Test
	@WithMockUser(username = username, roles = role)
	void deleteProfile() throws Exception {
		Assert.assertEquals(true, userRepo.findByUsername(username).isEnabled());

		mockMvc.perform(delete("/user/profile"))
				.andDo(print())
				.andExpect(status().isNoContent());

		Assert.assertEquals(null, userRepo.findByUsername(username));


	}

	// users

	@Test
	@WithMockUser(username = username, roles = role)
	void getUsers() throws Exception {

		mockMvc.perform(get("/user/users"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(userRepo.findAllByEnabled(true).size()));

	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void getUserById() throws Exception {
		Long userId = 99L;

		if(userRepo.findById(userId).isPresent()){
			mockMvc.perform(get("/user/users/" + userId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(userId));
		}else{
			mockMvc.perform(get("/user/users/" + userId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.*").doesNotExist());
		}
	}


	// ads

	@Test
	@WithMockUser(username = username, roles = role)
	void getAds() throws Exception {
		mockMvc.perform(get("/user/ads"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(adRepo.getAds().size()));

	}

	@Test
	@WithMockUser(username = username, roles = role)
	void getUserAds() throws Exception {

		Long userId = userRepo.findByUsername(username).getId();

		mockMvc.perform(get("/user/" + userId + "/ads"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(adRepo.getAds(userId).size()));
	}

	@Test
	@WithMockUser(username = username, roles = role)
	void addNewAd() throws Exception {
		mockMvc.perform(multipart("/user/ads").file("image", null).param("data", "{ \n" +
				"  \"title\":\"Test House\",\n" +
				" \"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\", \n" +
				"    \"pricePerMonth\": \"100\", \n" +
				"    \"totalRoomAmount\":\"4\",\n" +
				"    \"bathRoomAmount\":\"1\",\n" +
				"    \"bedRoomAmount\":\"2\",\n" +
				"     \"kitchenRoomAmount\":\"1\",\n" +
				"    \"area\":\"40\",\n" +
				"    \"whichFloor\":\"1\",\n" +
				"    \"furniture\":\"\",\n" +
				"    \"location\":\"Bishkek\",\n" +
				"    \"houseType\":\"Family House\" \n" +
				"}"))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"));
	}

	@Test
	@WithMockUser(username = username, roles = role)
	void editAd() throws Exception {

		String editedTitle = "New Title";

		Long adId = adRepo.findMaxId();

		if(adId == 0) {
			mockMvc.perform(get("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.*").doesNotExist());
			return;
		}

		if(adRepo.findById(adId).get().getRenter().getId() == userRepo.findByUsername(username).getId()) {
			mockMvc.perform(multipart("/user/ads").file("image", null).with(new RequestPostProcessor() {
						@Override
						public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
							request.setMethod("PUT");
							return request;
						}
					}).param("data", "{ \n" +
							"  \"id\":\"" + adId + "\",\n" +
							"  \"title\":\"" + editedTitle + "\",\n" +
							" \"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\", \n" +
							"    \"pricePerMonth\": \"100\", \n" +
							"    \"totalRoomAmount\":\"4\",\n" +
							"    \"bathRoomAmount\":\"1\",\n" +
							"    \"bedRoomAmount\":\"2\",\n" +
							"     \"kitchenRoomAmount\":\"1\",\n" +
							"    \"area\":\"40\",\n" +
							"    \"whichFloor\":\"1\",\n" +
							"    \"furniture\":\"\",\n" +
							"    \"location\":\"Bishkek\",\n" +
							"    \"houseType\":\"Family House\" \n" +
							"}"))
					.andDo(print())
					.andExpect(status().isNoContent());
			Assert.assertEquals(editedTitle, adRepo.findById(adId).get().getTitle());
		} else {
			mockMvc.perform(multipart("/user/ads").file("image", null).with(new RequestPostProcessor() {
						@Override
						public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
							request.setMethod("PUT");
							return request;
						}
					}).param("data", "{ \n" +
							"  \"id\":\"" + adId + "\",\n" +
							"  \"title\":\"" + editedTitle + "\",\n" +
							" \"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\", \n" +
							"    \"pricePerMonth\": \"100\", \n" +
							"    \"totalRoomAmount\":\"4\",\n" +
							"    \"bathRoomAmount\":\"1\",\n" +
							"    \"bedRoomAmount\":\"2\",\n" +
							"     \"kitchenRoomAmount\":\"1\",\n" +
							"    \"area\":\"40\",\n" +
							"    \"whichFloor\":\"1\",\n" +
							"    \"furniture\":\"\",\n" +
							"    \"location\":\"Bishkek\",\n" +
							"    \"houseType\":\"Family House\" \n" +
							"}"))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

	}

	@Test
	@WithMockUser(username = username, roles = role)
	void getAd() throws Exception {
		Long adId = 1L;
		if(adRepo.findById(adId).isPresent()){
			mockMvc.perform(get("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(adId));
		}else{
			mockMvc.perform(get("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.*").doesNotExist());
		}
	}

	@Test
	@WithMockUser(username = username, roles = role)
	void deleteAd() throws Exception {
		Long adId = adRepo.findMaxId();

		if(adId == 0) {
			mockMvc.perform(get("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.*").doesNotExist());
			return;
		}


		if(adRepo.findById(adId).get().getRenter().getId() == userRepo.findByUsername(username).getId()) {

			mockMvc.perform(get("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(adId));

			mockMvc.perform(delete("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isNoContent());

			mockMvc.perform(get("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.*").doesNotExist());

			// recovering for other tests

			addNewAd();
		} else {
			mockMvc.perform(delete("/user/ads/" + adId))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}


	}


	// house types
	@Test
	@WithMockUser(username = username, roles = role)
	void getHouseTypes() throws Exception {
		mockMvc.perform(get("/user/house-types"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(houseTypeRepo.findAll().size()));
	}


	@Test
	@WithMockUser(username = username, roles = role)
	void getHouseType() throws Exception {
		Long id = 1L;

		if(houseTypeRepo.findById(id).isPresent()){
			mockMvc.perform(get("/user/house-types/" + id))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(id));
		}else{
			mockMvc.perform(get("/user/house-types/" + id))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.*").doesNotExist());
		}

	}

	// promotions
	@Test
	@WithMockUser(username = username, roles = role)
	void getPromotionTypes() throws Exception {
		mockMvc.perform(get("/user/promotions"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(promotionTypeRepo.findAll().size()));
	}

	@Test
	@WithMockUser(username = username, roles = role)
	void getPromotionType() throws Exception {
		Long id = 1L;

		if(promotionTypeRepo.findById(id).isPresent()){
			mockMvc.perform(get("/user/promotions/" + id))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(id));
		}else{
			mockMvc.perform(get("/user/promotions/" + id))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.*").doesNotExist());
		}

	}

	@Test
	@WithMockUser(username = username, roles = role)
	void promote() throws Exception {
		Long promotionId;
		Long adId;
		long[][] list = new long[][]{{adRepo.findMaxId(), 1L}, {0, 1L}};

		for(long[] values : list) {
			adId = values[0];
			promotionId = values[1];

			if(adRepo.findById(adId).isPresent() && promotionTypeRepo.findById(promotionId).isPresent()){
				mockMvc.perform(post("/user/ads/" + adId + "/promotion/" + promotionId))
						.andDo(print())
						.andExpect(status().isNoContent());
			}else{
				mockMvc.perform(post("/user/ads/" + adId + "/promotion/" + promotionId))
						.andDo(print())
						.andExpect(status().isBadRequest());
			}
		}


	}


	// saved list
	@Test
	@WithMockUser(username = username, roles = role)
	void getMySavedList() throws Exception {
		mockMvc.perform(get("/user/saved-list"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(savedListRepo.findAllByRenteeId(userRepo.findByUsername(username).getId()).size()));
	}

	@Test
	@WithMockUser(username = username, roles = role)
	void triggerMySavedList() throws Exception {
		long[] values = new long[]{0, adRepo.findMaxId()};

		for(long value : values){
			if(adRepo.findById(value).isPresent()){
				// running first time

				mockMvc.perform(put("/user/saved-list/" + value))
						.andDo(print())
						.andExpect(status().isNoContent());
				if(savedListRepo.findByAdIdAndRenteeId(value, userRepo.findByUsername(username).getId()) == null) {
					Assert.assertNull(savedListRepo.findByAdIdAndRenteeId(value, userRepo.findByUsername(username).getId()));
				}else {
					Assert.assertNotNull(savedListRepo.findByAdIdAndRenteeId(value, userRepo.findByUsername(username).getId()));
				}

				// running second time. Cus there are two behaving possibilities and all needs to be tested

				mockMvc.perform(put("/user/saved-list/" + value))
						.andDo(print())
						.andExpect(status().isNoContent());
				if(savedListRepo.findByAdIdAndRenteeId(value, userRepo.findByUsername(username).getId()) == null) {
					Assert.assertNull(savedListRepo.findByAdIdAndRenteeId(value, userRepo.findByUsername(username).getId()));
				}else {
					Assert.assertNotNull(savedListRepo.findByAdIdAndRenteeId(value, userRepo.findByUsername(username).getId()));
				}

			}else {
				mockMvc.perform(put("/user/saved-list/" + value))
						.andDo(print())
						.andExpect(status().isBadRequest());

			}
		}

	}
}
