#!/bin/bash

AWS_PROFILE=tech
SOURCE_DB_INSTANCE_IDENTIFIER=db-docker
DB_INSTANCE_CLASS=db.t2.micro
DB_AVAILABILITY_ZONE=eu-west-1a
DB_SUBNET_GROUP_NAME=sngrp-db
DB_SECURITY_GROUP_ID=sg-7ffad619
DB_USER_NAME=accepter
DB_SCHEMA_NAME=accepter

if ! which aws > /dev/null; then echo "uuid is required"; exit 1; fi
if ! which jq > /dev/null; then echo "uuid is required"; exit 1; fi
if ! which uuid > /dev/null; then echo "uuid is required"; exit 1; fi
if ! which pwgen > /dev/null; then echo "uuid is required"; exit 1; fi
if ! which mysql > /dev/null; then echo "uuid is required"; exit 1; fi

DB_INSTANCE_IDENTIFIER="test-`uuid | cut -c -8`"
echo -e "Creating database \e[96m$DB_INSTANCE_IDENTIFIER\e[39m"

DB_SNAPSHOT_IDENTIFIER=`aws --profile $AWS_PROFILE rds describe-db-snapshots --db-instance-identifier $SOURCE_DB_INSTANCE_IDENTIFIER | jq -r ".DBSnapshots | sort_by(.SnapshotCreateTime) | .[] | select(.DBInstanceIdentifier == \"$SOURCE_DB_INSTANCE_IDENTIFIER\") | select (.Status == \"available\") | .DBSnapshotArn" | tail -n 1`
echo -e "Using snapshot \e[96m$DB_SNAPSHOT_IDENTIFIER\e[39m"

DB_INSTANCE_ARN=`aws --profile $AWS_PROFILE rds restore-db-instance-from-db-snapshot --db-subnet-group-name $DB_SUBNET_GROUP_NAME --db-instance-identifier "$DB_INSTANCE_IDENTIFIER" --db-snapshot-identifier "$DB_SNAPSHOT_IDENTIFIER" --db-instance-class $DB_INSTANCE_CLASS --availability-zone $DB_AVAILABILITY_ZONE --no-multi-az --publicly-accessible --no-auto-minor-version-upgrade | jq -r ".DBInstance.DBInstanceArn"`
echo -e "Created database with ARN \e[96m$DB_INSTANCE_ARN\e[39m"

DB_STATUS="unknown"; while ! echo "$DB_STATUS" | grep "available" > /dev/null; do echo -e "Waiting for database \e[96m$DB_INSTANCE_IDENTIFIER\e[39m to get available (current state is \e[93m$DB_STATUS\e[39m)..."; sleep 30; DB_STATUS="`aws --profile $AWS_PROFILE rds describe-db-instances --db-instance-identifier $DB_INSTANCE_IDENTIFIER | jq -r ".DBInstances[0].DBInstanceStatus"`"; done

DB_MASTER_USER_PASSWORD="`pwgen 16 1`"
DB_ENDPOINT_ADDRESS=`aws --profile $AWS_PROFILE rds modify-db-instance --db-instance-identifier "$DB_INSTANCE_IDENTIFIER" --vpc-security-group-ids $DB_SECURITY_GROUP_ID --master-user-password=$DB_MASTER_USER_PASSWORD --apply-immediately | jq -r ".DBInstance.Endpoint.Address"`
echo "Setting root password, security group and parameters..."
while [ `aws --profile $AWS_PROFILE rds describe-db-instances --db-instance-identifier $DB_INSTANCE_IDENTIFIER | jq ".DBInstances[0].PendingModifiedValues | length"` != 0 ]; do echo "Waiting for changes to get applied..."; sleep 30; done

DB_STATUS="unknown"; while ! echo "$DB_STATUS" | grep "available" > /dev/null; do echo -e "Waiting for database \e[96m$DB_INSTANCE_IDENTIFIER\e[39m to get available (current state is \e[93m$DB_STATUS\e[39m)..."; sleep 5; DB_STATUS="`aws --profile $AWS_PROFILE rds describe-db-instances --db-instance-identifier $DB_INSTANCE_IDENTIFIER | jq -r ".DBInstances[0].DBInstanceStatus"`"; done
echo -e "Database is listening at \e[96m$DB_ENDPOINT_ADDRESS\e[39m with root password \e[91m$DB_MASTER_USER_PASSWORD\e[39m"

DB_USER_PASSWORD="`pwgen 8 1`"
echo "SET PASSWORD FOR $DB_USER_NAME = PASSWORD('$DB_USER_PASSWORD');" | mysql -u root --password="$DB_MASTER_USER_PASSWORD" -h "$DB_ENDPOINT_ADDRESS" 2>&1 | grep -v "Using a password on the command line"
echo -e "Changed password of user \e[96m$DB_USER_NAME\e[39m to \e[91m$DB_USER_PASSWORD\e[39m"

echo -e "\nYou can connect to your instance using:"
echo -e "mysql -A -u root --password=$DB_MASTER_USER_PASSWORD -D $DB_SCHEMA_NAME -h $DB_ENDPOINT_ADDRESS"
echo -e "mysql -A -u $DB_USER_NAME --password=$DB_USER_PASSWORD -D $DB_SCHEMA_NAME -h $DB_ENDPOINT_ADDRESS"

