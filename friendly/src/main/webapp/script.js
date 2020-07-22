//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * This function is to get the login status of the user
 */
function getLoginStatus() {
    fetch('/login').then(response => response.json()).then((loginStatus) => {
        const loginElement = document.getElementById('login');
        const groupElement = document.getElementById('groups');
        
        loginElement.innerHTML = '<h4>Login Status: '+ loginStatus.loginStatus + '</h4>';
        if (loginStatus.loginStatus){
                loginElement.innerHTML = '<h4> Hello, ' + loginStatus.userEmail + '!</h4>'
                loginElement.appendChild(createRedirectButtonElement(loginStatus.logoutUrl, 'logout'));
                groupElement.style.display = "block";
                getUserGroups(loginStatus.userEmail);
        }
        else {
            loginElement.innerHTML = '<h4> Log in to view your groups </h4>'
            loginElement.appendChild(createRedirectButtonElement(loginStatus.loginUrl, 'login'));
            groupElement.style.display = "none";
        }
    })

}

/**
 * This function will query user's group list
 */
function getUserGroups() {
    fetch('/group').then(response => response.json()).then((groupList) => {
        const groupData = document.getElementById("data");
        if (groupList.length === 0) {
            groupData.innerHTML = '<h3>No groups Yet</h3>';
        }
        else {
            groupData.innerHTML = ('<h4>Your groups: </h4>');
            groupDropdown = document.createElement('select');
            groupDropdown.setAttribute("id", "groupName");
            groupDropdown.setAttribute("onchange","getRecommendationContainer(); getDeleteGroupContainer();",); 
            groupList.map(group => createDropdown(group)).map(element => groupDropdown.appendChild(element));
            groupData.appendChild(groupDropdown);
            getRecommendationContainer();
            getDeleteGroupContainer();
        }
    })
}

function createRedirectButtonElement(redirectUrl, buttonText) {
    const buttonElement = document.createElement('button');
    buttonElement.innerText = buttonText;
    buttonElement.addEventListener("click", function(){
        window.location.href = redirectUrl;
    });
    return buttonElement;
}

function createGroupForm() {
    var modal = document.getElementById("groupModal");
    modal.style.display = "block";
}

function closeGroupForm() {
    var modal = document.getElementById("groupModal");
    modal.style.display ="none";
}

function createDeleteForm() {
    var modal = document.getElementById("deleteModal");
    modal.style.display = "block";
}

function closeDeleteForm() {
    var modal = document.getElementById("deleteModal");
    modal.style.display ="none";
}

function createDropdown(text) {
    const dropdownElement = document.createElement('option');
    dropdownElement.innerText = text;
    return dropdownElement;
}

function createRecommendationForm() {
    var modal = document.getElementById("recommendationModal");
    modal.style.display = "block";
}

function closeRecommendationForm() {
    var modal=document.getElementById("recommendationModal");
    modal.style.display = "none";
}

/**
 * This function will show the recommendation container: recommendation list and add recommendation button
 */
function getRecommendationContainer() {

    //Get selected group name & pass to recommendation form as hidden input 
    groupList = document.getElementById('groupName')
    if (groupList != null) {
        groupName = groupList.options[groupList.selectedIndex].text;
        document.getElementById('groupNameInput').value = groupName;
        document.getElementById('recommendation-container').style.display = "block";
        getRecommendations()
    }
}

/**
 * This function will query the group's recommendations
 */
function getRecommendations() {

    locationList = document.getElementById('locationFilterName');
    locationName = locationList.options[locationList.selectedIndex].text;

    //Retrieve the groupName
    groupName = document.getElementById('groupNameInput').value;

    //Adding groupName to Query String
    query = '/recommendation' + '?groupName='+ groupName + '&locationName=' + locationName;
    
    fetch(query).then(response => response.json()).then(recommendations => {
        const recommendationElement = document.getElementById("recommendation-list");
        recommendationElement.innerHTML = "";

        //Convert each recommendation to html list
        recommendations.forEach(recommendation => {
            const liElement = document.createElement('li');
            liElement.innerText = recommendation.restaurantName + " " + recommendation.location;
            recommendationElement.append(liElement);
      });
        
    }); 
}

/**
 * This function will show the delete button when there are groups
 */
function getDeleteGroupContainer() {
    // Get whether there are groups or no groups
    groupList = document.getElementById('groupName')
    if (groupList != null) {
        document.getElementById('delete-group-container').style.display = "block";
    }
}

//Fetch the latest recommendation data every 3 seconds
window.setInterval(getRecommendations, 3000);

