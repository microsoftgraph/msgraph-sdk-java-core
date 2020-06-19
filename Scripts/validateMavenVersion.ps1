# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.

<# 
.Synopsis
    Ensure the maven version is updated in the case that the pull request is 
    to the main/master branch of the repo.
.Description 
#>



$fullFileName = $PWD.toString + "\gradle.properties"
$file = get-item $fullFileName
$findVersions = $file | Select-String -Pattern "mavenMajorVersion" -Context 0,2
$localVersions = $findVersions -split "`r`n"

$localMajorVersion = $localVersions[0]
$localMajorVersion = [int]$localMajorVersion.Substring($localMajorVersion.Length-1)

$localMinorVersion = $localVersions[1]
$localMinorVersion = [int]$localMinorVersion.Substring($localMinorVersion.Length-1)

$localPatchVersion = $localVersions[2]
$localPatchVersion = [int]$localPatchVersion.Substring($localPatchVersion.Length-1)


$web_client = New-Object System.Net.WebClient

$mavenAPIurl = 'https://search.maven.org/solrsearch/select?q=g:"com.microsoft.graph"+AND+a:"microsoft-graph-core"&core=gav&rows=20&wt=json'
$jsonResult = $web_client.DownloadString($mavenAPIurl) | ConvertFrom-Json
$mavenVersions = $jsonResult.response.docs.v.split(".")
$mavenMajorVersion = [int]$mavenVersions[0]
$mavenMinorVersion = [int]$mavenVersions[1]
$mavenPatchVersion = [int]$mavenVersions[2]

$bintrayAPIurl = 'https://api.bintray.com/search/packages?name=microsoft-graph-core'
$jsonResult = $web_client.DownloadString($bintrayAPIurl) | ConvertFrom-Json
$bintrayVersions = $jsonResult.latest_version.split(".")
$bintrayMajorVersion = [int]$bintrayVersions[0]
$bintrayMinorVersion = [int]$bintrayVersions[1]
$bintrayPatchVersion = [int]$bintrayVersions[2]

if(($bintrayMinorVersion -ne $mavenMinorVersion) -OR 
($bintrayMajorversion -ne $mavenMajorVersion) -OR 
($bintraypatchversion -ne $mavenpatchversion))
{
    "The current Maven and Bintray versions are not the same"
}
else {
    if(($localMajorVersion -gt $bintrayMajorVersion) -OR 
    ($localMinorVersion -gt $bintrayMinorVersion) -OR
    ($localPatchVersion -gt $bintrayPatchVersion))
    {
        "The current pull request is of a greater version"
    }    
}









