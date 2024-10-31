### Entry point
param (
    [string]$a, # action
    [string]$p  # profile
)

## environemnt variables
$local_directory_war = "C:\GDRIVE\dev\docker\docker.services.tomcat\wars\"
$remote_directory_war = "/usr/local/tomcat/wars/"
$war_un = 'tomcat'
$war_pw = 'B$LUEtr33sd'
$war_server = 'srv-sw-valtasd1.lands.nsw'
$war_file_name = "valsync"
$__sshfp = 'ssh-ed25519 255 IMEjchDPlhooerhwdR9oDA3XMlEtIQoLxEKnvgdy0YY'

$uat_un = 'valro'
$uat_pw = 'RE$Dtr33sq'
$uat_server = 'srv-sw-valtasq1.lands.nsw'

### libraries
Add-Type -Path "C:\Program Files (x86)\WinSCP\WinSCPnet.dll"

### utilities and helper functions
function Get-ServerSession{
    param (
        [string]$server
    )

    $sessionOptions = New-Object WinSCP.SessionOptions
    $sessionOptions.Protocol = [WinSCP.Protocol]::Sftp
    if(@("war","dev") -contains $server){
        $__server = $war_server
        $__un = $war_un
        $__pw = $war_pw        
    }elseif ($server -eq "uat") {
        $__server = $uat_server
        $__un = $uat_un
        $__pw = $uat_pw
    }    
    $sessionOptions.HostName = $__server
    $sessionOptions.UserName = $__un
    $sessionOptions.Password = $__pw        
    $sessionOptions.SshHostKeyFingerprint = $__sshfp
    $session = New-Object WinSCP.Session
    $session.Open($sessionOptions)
    return $session
}
# build project
function Build-Project {    
    param (
        [string]$build_profile
    )

    Write-Host "### Building Project ($($build_profile.toUpper())) ###"
    Write-Host  "1/2.  Compiling Source" -NoNewLine
    $command = "mvn clean package -P$build_profile"
    Invoke-Expression $command
    
    $__localfilename = "$war_file_name-$build_profile.war" 
    $__localfilepath = "./target/$__localfilename"        
    if(-not (Test-Path $__localfilepath)){
        Write-Host "    ==> Fail"
        exit 1
    }
    Write-Host "    ==> Pass"    
    Write-Host  "2/2.  Copy to War directory" -NoNewLine
    Copy-Item -Path $__localfilepath -Destination $local_directory_war -Force
    Write-Host "    ==> Pass $local_directory_war"
}

# upload to war file to developement server
function Upload-Server {
    param (
        [string]$build_profile
    )
    ## copy to local war directory
    Write-Host "### Uploading Developement Server ###"
    $__localfilename = "$war_file_name-$build_profile.war"     
    $__localfilepath = "$local_directory_war$__localfilename"
    $__remotefilepath = "$remote_directory_war$__localfilename"

    Write-Host  "1/2.  Testing file exists ($__localfilename)" -NoNewLine
    if(-not (Test-Path $__localfilepath)){
        Write-Host "  ==> Fail"
        return
    }
    Write-Host "  ==> Pass"        
    Write-Host "2/2.  Upload to Deployment server" -NoNewLine
    
    $sessionOptions = New-Object WinSCP.SessionOptions
    $sessionOptions.Protocol = [WinSCP.Protocol]::Sftp
    $sessionOptions.HostName = $war_server
    $sessionOptions.UserName = $war_un
    $sessionOptions.Password = $war_pw
    $sessionOptions.SshHostKeyFingerprint = $__sshfp

    # Create a new instance of the WinSCP session
    $session = New-Object WinSCP.Session
    try {
        # # Open the session
        $session.Open($sessionOptions)
        # Upload the file
        $transferOptions = New-Object WinSCP.TransferOptions
        $transferOptions.TransferMode = [WinSCP.TransferMode]::Binary
        $transferOptions.OverwriteMode = [WinSCP.OverwriteMode]::Overwrite

        $transferResult = $session.PutFiles($__localfilepath, $__remotefilepath, $False, $transferOptions)

        # Check if the transfer was successful
        if ($transferResult.IsSuccess) {
            Write-Host "    ==> Upload successful."
        } else {
            Write-Host "    ==> Upload failed."
        }
    }
    catch {
        Write-Host "    ==> Error: $_"
    }
    finally {
        # Ensure session is closed
        $session.Dispose()
    }
}

function Deploy-Server {
    param (
        [string]$build_profile = 'dev'
    )    
    Write-Host "##### Deploying to $($build_profile.toUpper()) Server #####"
        
    try {
        # Open the session
        ##$session.Open($sessionOptions)
        $session = Get-ServerSession $build_profile

        # if dev then remote in and copy war file to webapps dir
        if($build_profile -eq "dev"){
            $src_war_file = "$remote_directory_war$war_file_name-$build_profile.war"
            $dest_war_file = "/usr/local/tomcat/webapps/$war_file_name.war"
            $timestamp = Get-Date -Format "yyyyMMddHHmmss"
            $backup_war_file = "/usr/local/tomcat/webapps/$war_file_name.war.$timestamp"
            $command = 
"if [ -e $dest_war_file ]; then `n" + 
"   echo 'Backing up currently deployed == $war_file_name.war.$timestamp'`n" + 
"   mv $dest_war_file $backup_war_file`n" + 
"fi"
            $executionResults = $session.ExecuteCommand($command)            
            Write-Host $executionResults.Output

            $command = 
"echo 'Deploying'`n" + 
"cp $src_war_file $dest_war_file"

            $executionResults = $session.ExecuteCommand($command)            
            Write-Host $executionResults.Output            
        }else{
        # ssh in to remote server and scp from dev server
        $command = "~/deploy_valsync.sh"
        $executionResults = $session.ExecuteCommand($command)            
        Write-Host $executionResults.Output
        
        # $command = "cd .."
        # $executionResults = $session.ExecuteCommand($command)            
        # Write-Host $executionResults.Output

        # $command = "pwd"
        # $executionResults = $session.ExecuteCommand($command)            
        # Write-Host $executionResults.Output

#"sudo su - tomcat`n"
# "if [ -e $dest_war_file ]; then `n" + 
# "   echo 'Backing up currently deployed == $war_file_name.war.$timestamp'`n" + 
# "   mv $dest_war_file $backup_war_file`n" + 
# "fi"
        # $executionResults = $session.ExecuteCommand($command)            
        # Write-Host $executionResults.Output

        }                
    }
    finally {
        # Ensure the session is closed
        $session.Dispose()
    }

}

# Check if both arguments are passed
if (-not $a -or -not $p) {
    Write-Host "Error: Please pass in -a [build|upload|deploy|release] -p [dev|uat|prod]"
    exit 1  # Exit the script with a non-zero code to indicate failure
}

if (@("build","release") -contains $a) {
    Build-Project -build_profile $p
}

if (@("upload","release") -contains $a) {
    Upload-Server -build_profile $p
}

if (@("deploy","release") -contains $a) {
    Deploy-Server -build_profile $p
}

