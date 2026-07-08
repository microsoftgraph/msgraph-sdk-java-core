# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.

<# 
.Synopsis
    Retrieve the latest version of the library
.Description 
    Retrieves the latest version specified in the pom.xml file
    Uses the retrieved values to update the environment variable VERSION_STRING
.Parameter propertiesPath
    The path pointing to the pom.xml file.
#>

Param(
    [string]$propertiesPath
)

#Retrieve the current version from the pom.xml file given the specified path
if($propertiesPath -eq "" -or $null -eq $propertiesPath) {
    $propertiesPath = Join-Path -Path $PSScriptRoot -ChildPath "../pom.xml"
}

$pomXml = [xml](Get-Content $propertiesPath -Raw)
$ns = New-Object System.Xml.XmlNamespaceManager($pomXml.NameTable)
$ns.AddNamespace('m', $pomXml.DocumentElement.NamespaceURI)
$version = $pomXml.SelectSingleNode('/m:project/m:version', $ns).InnerText
$version = $version -replace '-SNAPSHOT$', ''

#Set Task output to create tag
Write-Output "::set-output name=tag::v${version}"