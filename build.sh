 #!/bin/bash

 # Load environment variables from .env file
 set -a
 source .env
 set +a

 # Define the build order (discovery service first)
 services=(
     "discovery-service"
     "authentication-service"
     "comments-service"
     "gateway-service"
     "kafka-service"
     "notifications-service"
     "sops-service"
 )

 # Function to get port variable name for a service
 get_port_var() {
     echo "${1^^}_PORT" | tr '-' '_'
 }

 # Build services
 for service in "${services[@]}"
 do
     port_var=$(get_port_var "$service")
     echo "Building $service using port ${!port_var}..."

     docker build \
         --build-arg JAVA_VERSION=$JAVA_VERSION \
         --build-arg APP_PORT=${!port_var} \
         --no-cache \
         -t "$DOCKER_REGISTRY/$PROJECT_NAME-$service:$VERSION" \
         -f $service/Dockerfile "$service"

     if [ $? -ne 0 ]; then
         echo "Error building $service"
         exit 1
     fi
 done

 echo "All services built successfully!"

 # Push images if PUSH_IMAGES is true
 if [ "$PUSH_IMAGES" = "true" ]; then
     for service in "${services[@]}"
     do
         echo "Pushing $service to registry..."
         docker push "$DOCKER_REGISTRY/$PROJECT_NAME-$service:$VERSION"

         if [ $? -ne 0 ]; then
             echo "Error pushing $service"
             exit 1
         fi
     done
     echo "All images pushed to registry!"
 fi