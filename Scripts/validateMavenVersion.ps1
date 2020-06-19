# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.

<# 
.Synopsis
    Ensure the maven version is updated in the case that the pull request is 
    to the main/master branch of the repo.
.Description 
#>

.Parameter packageName
.Parameter propertiesPath

Param(
    [parameter(Mandatory = $true)]
    [string]$packageName,

    [parameter(Mandatory = $true)]
    [string]$propertiesPath
)

$file = get-item $propertiesPath
$findVersions = $file | Select-String -Pattern "mavenMajorVersion" -Context 0,2
$localVersions = $findVersions -split "`r`n"

$localMajorVersion = $localVersions[0]
$localMajorVersion = [int]$localMajorVersion.Substring($localMajorVersion.Length-1)

$localMinorVersion = $localVersions[1]
$localMinorVersion = [int]$localMinorVersion.Substring($localMinorVersion.Length-1)

$localPatchVersion = $localVersions[2]
$localPatchVersion = [int]$localPatchVersion.Substring($localPatchVersion.Length-1)

$web_client = New-Object System.Net.WebClient

$mavenAPIurl = 'https://search.maven.org/solrsearch/select?q=$packageName&rows=20&wt=json'
$jsonResult = $web_client.DownloadString($mavenAPIurl) | ConvertFrom-Json
$mavenVersions = $jsonResult.response.docs.v
$mavenSplit = $mavenVersions.split(".")
$mavenMajorVersion = [int]$mavenSplit[0]
$mavenMinorVersion = [int]$mavenSplit[1]
$mavenPatchVersion = [int]$mavenSplit[2]

$bintrayAPIurl = 'https://api.bintray.com/search/packages?name=$packageName'
$jsonResult = $web_client.DownloadString($bintrayAPIurl) | ConvertFrom-Json
$bintrayVersions = $jsonResult.latest_version
$bintraySplit = $bintrayVersions.split(".")
$bintrayMajorVersion = [int]$bintraySplit[0]
$bintrayMinorVersion = [int]$bintraySplit[1]
$bintrayPatchVersion = [int]$bintraySplit[2]

write-host 'The current version in the Maven central repository is:' $mavenVersions
write-host 'The current version in the Bintray central repository is:' $bintrayVersions

if(($bintrayMinorVersion -ne $mavenMinorVersion) -OR 
($bintrayMajorversion -ne $mavenMajorVersion) -OR 
($bintraypatchversion -ne $mavenpatchversion)){
    Write-Warning "The current Maven and Bintray versions are not the same"
}

if(($localMajorVersion -gt $bintrayMajorVersion) -OR 
    ($localMinorVersion -gt $bintrayMinorVersion) -OR
    ($localPatchVersion -gt $bintrayPatchVersion)){
    Write-Host "The current pull request is of a greater version"
}   
else{
    Write-Error "The local version has not been updated or is of an earlier version than that on the remote repository"
} 









