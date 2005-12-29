begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
package|;
end_package

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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|command
operator|.
name|ActiveMQDestination
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
name|command
operator|.
name|ConnectionInfo
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
name|command
operator|.
name|SessionId
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
name|command
operator|.
name|SessionInfo
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
specifier|public
class|class
name|ConnectionState
block|{
specifier|final
name|ConnectionInfo
name|info
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
name|sessions
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
name|tempDestinations
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|ConnectionState
parameter_list|(
name|ConnectionInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
comment|// Add the default session id.
name|addSession
argument_list|(
operator|new
name|SessionInfo
argument_list|(
name|info
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|info
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|addTempDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|tempDestinations
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeTempDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|tempDestinations
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addSession
parameter_list|(
name|SessionInfo
name|info
parameter_list|)
block|{
name|sessions
operator|.
name|put
argument_list|(
name|info
operator|.
name|getSessionId
argument_list|()
argument_list|,
operator|new
name|SessionState
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SessionState
name|removeSession
parameter_list|(
name|SessionId
name|id
parameter_list|)
block|{
return|return
operator|(
name|SessionState
operator|)
name|sessions
operator|.
name|remove
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|SessionState
name|getSessionState
parameter_list|(
name|SessionId
name|id
parameter_list|)
block|{
return|return
operator|(
name|SessionState
operator|)
name|sessions
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|ConnectionInfo
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
specifier|public
name|Set
name|getSessionIds
parameter_list|()
block|{
return|return
name|sessions
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|List
name|getTempDesinations
parameter_list|()
block|{
return|return
name|tempDestinations
return|;
block|}
specifier|public
name|Collection
name|getSessionStates
parameter_list|()
block|{
return|return
name|sessions
operator|.
name|values
argument_list|()
return|;
block|}
block|}
end_class

end_unit

