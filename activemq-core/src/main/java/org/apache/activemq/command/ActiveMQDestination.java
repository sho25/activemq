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
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jndi
operator|.
name|JNDIBaseStorable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IntrospectionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|URISupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Externalizable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|ActiveMQDestination
extends|extends
name|JNDIBaseStorable
implements|implements
name|DataStructure
implements|,
name|Destination
implements|,
name|Externalizable
implements|,
name|Comparable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3885260014960795889L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATH_SEPERATOR
init|=
literal|"."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|char
name|COMPOSITE_SEPERATOR
init|=
literal|','
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|QUEUE_TYPE
init|=
literal|0x01
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|TOPIC_TYPE
init|=
literal|0x02
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|TEMP_MASK
init|=
literal|0x04
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|TEMP_TOPIC_TYPE
init|=
name|TOPIC_TYPE
operator||
name|TEMP_MASK
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|TEMP_QUEUE_TYPE
init|=
name|QUEUE_TYPE
operator||
name|TEMP_MASK
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_QUALIFIED_PREFIX
init|=
literal|"queue://"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOPIC_QUALIFIED_PREFIX
init|=
literal|"topic://"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEMP_QUEUE_QUALIFED_PREFIX
init|=
literal|"temp-queue://"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEMP_TOPIC_QUALIFED_PREFIX
init|=
literal|"temp-topic://"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEMP_DESTINATION_NAME_PREFIX
init|=
literal|"ID:"
decl_stmt|;
specifier|protected
name|String
name|physicalName
decl_stmt|;
specifier|transient
specifier|protected
name|ActiveMQDestination
index|[]
name|compositeDestinations
decl_stmt|;
specifier|transient
specifier|protected
name|String
index|[]
name|destinationPaths
decl_stmt|;
specifier|transient
specifier|protected
name|boolean
name|isPattern
decl_stmt|;
specifier|transient
specifier|protected
name|int
name|hashValue
decl_stmt|;
specifier|protected
name|Map
name|options
decl_stmt|;
comment|// static helper methods for working with destinations
comment|// -------------------------------------------------------------------------
specifier|static
specifier|public
name|ActiveMQDestination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
name|defaultType
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|QUEUE_QUALIFIED_PREFIX
argument_list|)
condition|)
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|QUEUE_QUALIFIED_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|TOPIC_QUALIFIED_PREFIX
argument_list|)
condition|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|TOPIC_QUALIFIED_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|TEMP_QUEUE_QUALIFED_PREFIX
argument_list|)
condition|)
block|{
return|return
operator|new
name|ActiveMQTempQueue
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|TEMP_QUEUE_QUALIFED_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|TEMP_TOPIC_QUALIFED_PREFIX
argument_list|)
condition|)
block|{
return|return
operator|new
name|ActiveMQTempTopic
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|TEMP_TOPIC_QUALIFED_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
switch|switch
condition|(
name|defaultType
condition|)
block|{
case|case
name|QUEUE_TYPE
case|:
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
return|;
case|case
name|TOPIC_TYPE
case|:
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
case|case
name|TEMP_QUEUE_TYPE
case|:
return|return
operator|new
name|ActiveMQTempQueue
argument_list|(
name|name
argument_list|)
return|;
case|case
name|TEMP_TOPIC_TYPE
case|:
return|return
operator|new
name|ActiveMQTempTopic
argument_list|(
name|name
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid default destination type: "
operator|+
name|defaultType
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|ActiveMQDestination
name|transform
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|dest
operator|instanceof
name|ActiveMQDestination
condition|)
return|return
operator|(
name|ActiveMQDestination
operator|)
name|dest
return|;
if|if
condition|(
name|dest
operator|instanceof
name|TemporaryQueue
condition|)
return|return
operator|new
name|ActiveMQTempQueue
argument_list|(
operator|(
operator|(
name|TemporaryQueue
operator|)
name|dest
operator|)
operator|.
name|getQueueName
argument_list|()
argument_list|)
return|;
if|if
condition|(
name|dest
operator|instanceof
name|TemporaryTopic
condition|)
return|return
operator|new
name|ActiveMQTempTopic
argument_list|(
operator|(
operator|(
name|TemporaryTopic
operator|)
name|dest
operator|)
operator|.
name|getTopicName
argument_list|()
argument_list|)
return|;
if|if
condition|(
name|dest
operator|instanceof
name|Queue
condition|)
return|return
operator|new
name|ActiveMQQueue
argument_list|(
operator|(
operator|(
name|Queue
operator|)
name|dest
operator|)
operator|.
name|getQueueName
argument_list|()
argument_list|)
return|;
if|if
condition|(
name|dest
operator|instanceof
name|Topic
condition|)
return|return
operator|new
name|ActiveMQTopic
argument_list|(
operator|(
operator|(
name|Topic
operator|)
name|dest
operator|)
operator|.
name|getTopicName
argument_list|()
argument_list|)
return|;
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Could not transform the destination into a ActiveMQ destination: "
operator|+
name|dest
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|ActiveMQDestination
name|destination2
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|==
name|destination2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|destination2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|==
name|destination2
operator|.
name|isQueue
argument_list|()
condition|)
block|{
return|return
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|destination2
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|isQueue
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
block|}
block|}
specifier|public
name|ActiveMQDestination
parameter_list|()
block|{     }
specifier|protected
name|ActiveMQDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|setPhysicalName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
parameter_list|(
name|ActiveMQDestination
name|composites
index|[]
parameter_list|)
block|{
name|setCompositeDestinations
argument_list|(
name|composites
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|instanceof
name|ActiveMQDestination
condition|)
block|{
return|return
name|compare
argument_list|(
name|this
argument_list|,
operator|(
name|ActiveMQDestination
operator|)
name|that
argument_list|)
return|;
block|}
if|if
condition|(
name|that
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|public
name|boolean
name|isComposite
parameter_list|()
block|{
return|return
name|compositeDestinations
operator|!=
literal|null
return|;
block|}
specifier|public
name|ActiveMQDestination
index|[]
name|getCompositeDestinations
parameter_list|()
block|{
return|return
name|compositeDestinations
return|;
block|}
specifier|public
name|void
name|setCompositeDestinations
parameter_list|(
name|ActiveMQDestination
index|[]
name|destinations
parameter_list|)
block|{
name|this
operator|.
name|compositeDestinations
operator|=
name|destinations
expr_stmt|;
name|this
operator|.
name|destinationPaths
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|hashValue
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|isPattern
operator|=
literal|false
expr_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
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
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
name|COMPOSITE_SEPERATOR
argument_list|)
expr_stmt|;
if|if
condition|(
name|getDestinationType
argument_list|()
operator|==
name|destinations
index|[
name|i
index|]
operator|.
name|getDestinationType
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|destinations
index|[
name|i
index|]
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|destinations
index|[
name|i
index|]
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|physicalName
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getQualifiedName
parameter_list|()
block|{
if|if
condition|(
name|isComposite
argument_list|()
condition|)
return|return
name|physicalName
return|;
return|return
name|getQualifiedPrefix
argument_list|()
operator|+
name|physicalName
return|;
block|}
specifier|abstract
specifier|protected
name|String
name|getQualifiedPrefix
parameter_list|()
function_decl|;
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getPhysicalName
parameter_list|()
block|{
return|return
name|physicalName
return|;
block|}
specifier|public
name|void
name|setPhysicalName
parameter_list|(
name|String
name|physicalName
parameter_list|)
block|{
specifier|final
name|int
name|len
init|=
name|physicalName
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|p
init|=
operator|-
literal|1
decl_stmt|;
comment|// options offset
name|boolean
name|composite
init|=
literal|false
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|physicalName
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'?'
condition|)
block|{
name|p
operator|=
name|i
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|c
operator|==
name|COMPOSITE_SEPERATOR
condition|)
block|{
comment|// won't be wild card
name|isPattern
operator|=
literal|false
expr_stmt|;
name|composite
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|composite
operator|&&
operator|(
name|c
operator|==
literal|'*'
operator|||
name|c
operator|==
literal|'>'
operator|)
condition|)
block|{
name|isPattern
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// Strip off any options
if|if
condition|(
name|p
operator|>=
literal|0
condition|)
block|{
name|String
name|optstring
init|=
name|physicalName
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
decl_stmt|;
name|physicalName
operator|=
name|physicalName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|options
operator|=
name|URISupport
operator|.
name|parseQuery
argument_list|(
name|optstring
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid destination name: "
operator|+
name|physicalName
operator|+
literal|", it's options are not encoded properly: "
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|physicalName
operator|=
name|physicalName
expr_stmt|;
name|this
operator|.
name|destinationPaths
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|hashValue
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|composite
condition|)
block|{
comment|// Check to see if it is a composite.
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|iter
init|=
operator|new
name|StringTokenizer
argument_list|(
name|physicalName
argument_list|,
literal|""
operator|+
name|COMPOSITE_SEPERATOR
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|iter
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|l
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|compositeDestinations
operator|=
operator|new
name|ActiveMQDestination
index|[
name|l
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|dest
range|:
name|l
control|)
block|{
name|compositeDestinations
index|[
name|counter
operator|++
index|]
operator|=
name|createDestination
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|ActiveMQDestination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|createDestination
argument_list|(
name|name
argument_list|,
name|getDestinationType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
index|[]
name|getDestinationPaths
parameter_list|()
block|{
if|if
condition|(
name|destinationPaths
operator|!=
literal|null
condition|)
return|return
name|destinationPaths
return|;
name|ArrayList
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|StringTokenizer
name|iter
init|=
operator|new
name|StringTokenizer
argument_list|(
name|physicalName
argument_list|,
name|PATH_SEPERATOR
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|iter
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|l
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|destinationPaths
operator|=
operator|new
name|String
index|[
name|l
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|l
operator|.
name|toArray
argument_list|(
name|destinationPaths
argument_list|)
expr_stmt|;
return|return
name|destinationPaths
return|;
block|}
specifier|abstract
specifier|public
name|byte
name|getDestinationType
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isQueue
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isTopic
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isTemporary
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ActiveMQDestination
name|d
init|=
operator|(
name|ActiveMQDestination
operator|)
name|o
decl_stmt|;
return|return
name|physicalName
operator|.
name|equals
argument_list|(
name|d
operator|.
name|physicalName
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashValue
operator|==
literal|0
condition|)
block|{
name|hashValue
operator|=
name|physicalName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hashValue
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getQualifiedName
argument_list|()
return|;
block|}
specifier|public
name|void
name|writeExternal
parameter_list|(
name|ObjectOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|this
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|readExternal
parameter_list|(
name|ObjectInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|this
operator|.
name|setPhysicalName
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|options
operator|=
operator|(
name|Map
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getDestinationTypeAsString
parameter_list|()
block|{
switch|switch
condition|(
name|getDestinationType
argument_list|()
condition|)
block|{
case|case
name|QUEUE_TYPE
case|:
return|return
literal|"Queue"
return|;
case|case
name|TOPIC_TYPE
case|:
return|return
literal|"Topic"
return|;
case|case
name|TEMP_QUEUE_TYPE
case|:
return|return
literal|"TempQueue"
return|;
case|case
name|TEMP_TOPIC_TYPE
case|:
return|return
literal|"TempTopic"
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid destination type: "
operator|+
name|getDestinationType
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Map
name|getOptions
parameter_list|()
block|{
return|return
name|options
return|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|buildFromProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
block|}
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|populateProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"physicalName"
argument_list|,
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPattern
parameter_list|()
block|{
return|return
name|isPattern
return|;
block|}
block|}
end_class

end_unit

