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
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|ActiveMQConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ActiveMQTempDestination
extends|extends
name|ActiveMQDestination
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActiveMQTempDestination
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|transient
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|protected
specifier|transient
name|String
name|connectionId
decl_stmt|;
specifier|protected
specifier|transient
name|int
name|sequenceId
decl_stmt|;
specifier|public
name|ActiveMQTempDestination
parameter_list|()
block|{     }
specifier|public
name|ActiveMQTempDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQTempDestination
parameter_list|(
name|String
name|connectionId
parameter_list|,
name|long
name|sequenceId
parameter_list|)
block|{
name|super
argument_list|(
name|connectionId
operator|+
literal|":"
operator|+
name|sequenceId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTemporary
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|deleteTempDestination
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ActiveMQConnection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
specifier|public
name|void
name|setConnection
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
specifier|public
name|void
name|setPhysicalName
parameter_list|(
name|String
name|physicalName
parameter_list|)
block|{
name|super
operator|.
name|setPhysicalName
argument_list|(
name|physicalName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isComposite
argument_list|()
condition|)
block|{
comment|// Parse off the sequenceId off the end.
comment|// this can fail if the temp destination is
comment|// generated by another JMS system via the JMS<->JMS Bridge
name|int
name|p
init|=
name|this
operator|.
name|physicalName
operator|.
name|lastIndexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>=
literal|0
condition|)
block|{
name|String
name|seqStr
init|=
name|this
operator|.
name|physicalName
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|seqStr
operator|!=
literal|null
operator|&&
name|seqStr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|sequenceId
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|seqStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not parse sequence Id from "
operator|+
name|physicalName
argument_list|)
expr_stmt|;
block|}
comment|// The rest should be the connection id.
name|connectionId
operator|=
name|this
operator|.
name|physicalName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|String
name|getConnectionId
parameter_list|()
block|{
return|return
name|connectionId
return|;
block|}
specifier|public
name|void
name|setConnectionId
parameter_list|(
name|String
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
block|}
specifier|public
name|int
name|getSequenceId
parameter_list|()
block|{
return|return
name|sequenceId
return|;
block|}
block|}
end_class

end_unit

