<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

<!-- PROJECT LOGO -->
<br />
<div align="center">
 <!-- <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a> -->

  <h3 align="center">Custom Streampipes Processors</h3>

  <p align="center">
    Custom Processors developed by GFT for Open Source Project Streampipes
    <br />
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This is a custom extension of StreamPipes for integration of custom components in the main project


<!-- GETTING STARTED -->
## Getting Started

Step by Step guide to integration

### Prerequisites

Streampipes project 0.69.0 version found at: <a href="https://github.com/apache/incubator-streampipes">Streampipes</a>

### Installation
To integrate the project download it and place it under the streampipes-extensions folder

1. add the following line to the pom.xml file of the streampipes-extensions folder
  ```sh
   <module>streampipes-gft-processors</module>
   ```
2. In the streampipes-elements-all-jvm folder open the Pom.xml file and put this in the dependencies
   ```sh
   <dependency>
       <groupId>org.gft</groupId>
       <artifactId>streampipes-gft-processors</artifactId>
       <version>1.0-SNAPSHOT</version>
       <classifier>embed</classifier>
   </dependecy>
   ```
3. build the Streampipes folder from root
  ```sh
   mvn clean package
   ```
   
4. Go to the streampipes-extensions-all-jvm folder and run
   ```sh
   docker build -t YOUR_DOCKER_REGISTRY/DOCKER_IMAGE_CUSTOM_NAME
   ```
   Default:
   ```sh
   docker build -t YOUR_DOCKER_REGISTRY/streampipes-gft-extensions
   ```
5. Push your Docker image to the registry
   ```sh
   docker push YOUR_DOCKER_REGISTRY/DOCKER_IMAGE_CUSTOM_NAME
   ```
6. Go to the installer/compose folder, opend the docker-compose.yml file and modify the image path of the Streampipes-extensions-all image
   ```sh
   module: "YOUR_DOCKER_REGISTRY/DOCKER_IMAGE_CUSTOM_NAME:latest"
   ```
7. go to the installer/cli folder and run the following commands
   ```sh
   ./streampipes env -s pipeline-elements
   
   ./streampipes up -d
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

This is used to integrate the components developed by GFT in the OpenSource project Streampipes, but can also be used as a template to integrate any other custom componets

<p align="right">(<a href="#readme-top">back to top</a>)</p>
