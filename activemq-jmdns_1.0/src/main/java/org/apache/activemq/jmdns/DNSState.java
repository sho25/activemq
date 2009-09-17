begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2003-2005 Arthur van Hoff, Rick Blair  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jmdns
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
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * DNSState defines the possible states for services registered with JmDNS.  *  * @author Werner Randelshofer, Rick Blair  * @version 1.0  May 23, 2004  Created.  */
end_comment

begin_class
specifier|public
class|class
name|DNSState
implements|implements
name|Comparable
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DNSState
operator|.
name|class
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Ordinal of next state to be created.      */
specifier|private
specifier|static
name|int
name|nextOrdinal
init|=
literal|0
decl_stmt|;
comment|/**      * Assign an ordinal to this state.      */
specifier|private
specifier|final
name|int
name|ordinal
init|=
name|nextOrdinal
operator|++
decl_stmt|;
comment|/**      * Logical sequence of states.      * The sequence is consistent with the ordinal of a state.      * This is used for advancing through states.      */
specifier|private
specifier|final
specifier|static
name|ArrayList
name|sequence
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|DNSState
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|sequence
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
specifier|static
specifier|final
name|DNSState
name|PROBING_1
init|=
operator|new
name|DNSState
argument_list|(
literal|"probing 1"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DNSState
name|PROBING_2
init|=
operator|new
name|DNSState
argument_list|(
literal|"probing 2"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DNSState
name|PROBING_3
init|=
operator|new
name|DNSState
argument_list|(
literal|"probing 3"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DNSState
name|ANNOUNCING_1
init|=
operator|new
name|DNSState
argument_list|(
literal|"announcing 1"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DNSState
name|ANNOUNCING_2
init|=
operator|new
name|DNSState
argument_list|(
literal|"announcing 2"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DNSState
name|ANNOUNCED
init|=
operator|new
name|DNSState
argument_list|(
literal|"announced"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DNSState
name|CANCELED
init|=
operator|new
name|DNSState
argument_list|(
literal|"canceled"
argument_list|)
decl_stmt|;
comment|/**      * Returns the next advanced state.      * In general, this advances one step in the following sequence: PROBING_1,      * PROBING_2, PROBING_3, ANNOUNCING_1, ANNOUNCING_2, ANNOUNCED.      * Does not advance for ANNOUNCED and CANCELED state.      */
specifier|public
specifier|final
name|DNSState
name|advance
parameter_list|()
block|{
return|return
operator|(
name|isProbing
argument_list|()
operator|||
name|isAnnouncing
argument_list|()
operator|)
condition|?
operator|(
name|DNSState
operator|)
name|sequence
operator|.
name|get
argument_list|(
name|ordinal
operator|+
literal|1
argument_list|)
else|:
name|this
return|;
block|}
comment|/**      * Returns to the next reverted state.      * All states except CANCELED revert to PROBING_1.      * Status CANCELED does not revert.      */
specifier|public
specifier|final
name|DNSState
name|revert
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|CANCELED
operator|)
condition|?
name|this
else|:
name|PROBING_1
return|;
block|}
comment|/**      * Returns true, if this is a probing state.      */
specifier|public
name|boolean
name|isProbing
parameter_list|()
block|{
return|return
name|compareTo
argument_list|(
name|PROBING_1
argument_list|)
operator|>=
literal|0
operator|&&
name|compareTo
argument_list|(
name|PROBING_3
argument_list|)
operator|<=
literal|0
return|;
block|}
comment|/**      * Returns true, if this is an announcing state.      */
specifier|public
name|boolean
name|isAnnouncing
parameter_list|()
block|{
return|return
name|compareTo
argument_list|(
name|ANNOUNCING_1
argument_list|)
operator|>=
literal|0
operator|&&
name|compareTo
argument_list|(
name|ANNOUNCING_2
argument_list|)
operator|<=
literal|0
return|;
block|}
comment|/**      * Returns true, if this is an announced state.      */
specifier|public
name|boolean
name|isAnnounced
parameter_list|()
block|{
return|return
name|compareTo
argument_list|(
name|ANNOUNCED
argument_list|)
operator|==
literal|0
return|;
block|}
comment|/**      * Compares two states.      * The states compare as follows:      * PROBING_1&lt; PROBING_2&lt; PROBING_3&lt; ANNOUNCING_1&lt;      * ANNOUNCING_2&lt; RESPONDING&lt; ANNOUNCED&lt; CANCELED.      */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|ordinal
operator|-
operator|(
operator|(
name|DNSState
operator|)
name|o
operator|)
operator|.
name|ordinal
return|;
block|}
block|}
end_class

end_unit

