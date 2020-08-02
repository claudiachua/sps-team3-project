// Copyright 2019 Google LLC
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

package com.google.sps.model;

// Recommendation class
public final class Recommendation {

    private final String restaurantName;
    private final String location;
    private final String price;
    private final String cuisine;

    public Recommendation(String restaurantName, String location, String price, String cuisine) {
        this.restaurantName = restaurantName;
        this.location = location;
        this.price = price;
        this.cuisine = cuisine;
  }
}