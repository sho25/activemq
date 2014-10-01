begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|mqtt
package|;
end_package

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|CONNECT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|DISCONNECT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PINGREQ
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PUBACK
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PUBCOMP
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PUBLISH
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PUBREC
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PUBREL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|SUBSCRIBE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|UNSUBSCRIBE
import|;
end_import

begin_comment
comment|/**  * A set of static methods useful for handling MQTT based client connections.  */
end_comment

begin_class
specifier|public
class|class
name|MQTTProtocolSupport
block|{
comment|/**      * Converts an MQTT formatted Topic name into a suitable ActiveMQ Destination      * name string.      *      * @param name      *        the MQTT formatted topic name.      *      * @return an destination name that fits the ActiveMQ conventions.      */
specifier|public
specifier|static
name|String
name|convertMQTTToActiveMQ
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
name|name
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|chars
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'#'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'>'
expr_stmt|;
break|break;
case|case
literal|'>'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'#'
expr_stmt|;
break|break;
case|case
literal|'+'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'*'
expr_stmt|;
break|break;
case|case
literal|'*'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'+'
expr_stmt|;
break|break;
case|case
literal|'/'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'.'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'/'
expr_stmt|;
break|break;
block|}
block|}
name|String
name|rc
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
comment|/**      * Converts an ActiveMQ destination name into a correctly formatted      * MQTT destination name.      *      * @param destinationName      *        the ActiveMQ destination name to process.      *      * @return a destination name formatted for MQTT.      */
specifier|public
specifier|static
name|String
name|convertActiveMQToMQTT
parameter_list|(
name|String
name|destinationName
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
name|destinationName
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|chars
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'>'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'#'
expr_stmt|;
break|break;
case|case
literal|'#'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'>'
expr_stmt|;
break|break;
case|case
literal|'*'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'+'
expr_stmt|;
break|break;
case|case
literal|'+'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'*'
expr_stmt|;
break|break;
case|case
literal|'.'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'/'
expr_stmt|;
break|break;
case|case
literal|'/'
case|:
name|chars
index|[
name|i
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
block|}
block|}
name|String
name|rc
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
comment|/**      * Given an MQTT header byte, determine the command type that the header      * represents.      *      * @param header      *        the byte value for the MQTT frame header.      *      * @return a string value for the given command type.      */
specifier|public
specifier|static
name|String
name|commandType
parameter_list|(
name|byte
name|header
parameter_list|)
block|{
name|byte
name|messageType
init|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|header
operator|&
literal|0xF0
operator|)
operator|>>>
literal|4
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|messageType
condition|)
block|{
case|case
name|PINGREQ
operator|.
name|TYPE
case|:
return|return
literal|"PINGREQ"
return|;
case|case
name|CONNECT
operator|.
name|TYPE
case|:
return|return
literal|"CONNECT"
return|;
case|case
name|DISCONNECT
operator|.
name|TYPE
case|:
return|return
literal|"DISCONNECT"
return|;
case|case
name|SUBSCRIBE
operator|.
name|TYPE
case|:
return|return
literal|"SUBSCRIBE"
return|;
case|case
name|UNSUBSCRIBE
operator|.
name|TYPE
case|:
return|return
literal|"UNSUBSCRIBE"
return|;
case|case
name|PUBLISH
operator|.
name|TYPE
case|:
return|return
literal|"PUBLISH"
return|;
case|case
name|PUBACK
operator|.
name|TYPE
case|:
return|return
literal|"PUBACK"
return|;
case|case
name|PUBREC
operator|.
name|TYPE
case|:
return|return
literal|"PUBREC"
return|;
case|case
name|PUBREL
operator|.
name|TYPE
case|:
return|return
literal|"PUBREL"
return|;
case|case
name|PUBCOMP
operator|.
name|TYPE
case|:
return|return
literal|"PUBCOMP"
return|;
default|default:
return|return
literal|"UNKNOWN"
return|;
block|}
block|}
block|}
end_class

end_unit

