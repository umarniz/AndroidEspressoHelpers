#!/bin/bash

adb devices

# Uninstall all nl.sense_os.* packages from all connected phones

PACKAGE_TO_REMOVE="nl.sense_os.";

# Remove all apps with $PACKAGE_TO_REMOVE ids

echo "Started to loop over the connected android devices"
echo ""
for SERIAL in $(adb devices | grep -v List | cut -f 1);
	do
        deviceProps=`adb -s $SERIAL shell getprop ro.product.model`;
        model=`echo $deviceProps`;

		echo "Uninstalling following packages from device $model";
		adb -s $SERIAL shell pm list packages | grep $PACKAGE_TO_REMOVE | xargs -L 1 echo "---";
        adb -s $SERIAL shell pm list packages | grep $PACKAGE_TO_REMOVE | cut -f 2 -d :  | perl -pe 's/\r//' | xargs -L 1 -n 1 adb -s $SERIAL uninstall  | xargs -L 1 echo "-----";

        echo ""
done
