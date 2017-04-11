sudo -v

while true; do sudo -n true; sleep 60; kill -0 "$$" || exit; done 2> /dev/null &

sudo pmset -a standbydelay 86400

sudo systemsetup -setcomputersleep Off > /dev/null

sudo rm /private/var/vm/sleepimage

sudo touch /private/var/vm/sleepimage

sudo chflags uchg /private/var/vm/sleepimage

sudo pmset -a sms 0

defaults write com.apple.screencapture location -string "${HOME}/Desktop"

defaults write com.apple.screencapture type -string "png"

defaults write com.apple.finder ShowPathbar -bool true

defaults write com.apple.finder ShowStatusBar -bool true

defaults write com.apple.terminal StringEncodings -array 4

defaults write com.google.Chrome AppleEnableMouseSwipeNavigateWithScrolls -bool false


