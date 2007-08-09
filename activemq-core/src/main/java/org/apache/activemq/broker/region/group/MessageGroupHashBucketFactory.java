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
name|broker
operator|.
name|region
operator|.
name|group
package|;
end_package

begin_comment
comment|/**  * A factory to create instances of {@link SimpleMessageGroupMap} when  * implementing the<a  * href="http://activemq.apache.org/message-groups.html">Message  * Groups</a> functionality.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MessageGroupHashBucketFactory
implements|implements
name|MessageGroupMapFactory
block|{
specifier|private
name|int
name|bucketCount
init|=
literal|1024
decl_stmt|;
specifier|public
name|MessageGroupMap
name|createMessageGroupMap
parameter_list|()
block|{
return|return
operator|new
name|MessageGroupHashBucket
argument_list|(
name|bucketCount
argument_list|)
return|;
block|}
specifier|public
name|int
name|getBucketCount
parameter_list|()
block|{
return|return
name|bucketCount
return|;
block|}
comment|/**      * Sets the number of hash buckets to use for the message group      * functionality. This is only applicable to using message groups to      * parallelize processing of a queue while preserving order across an      * individual JMSXGroupID header value. This value sets the number of hash      * buckets that will be used (i.e. the maximum possible concurrency).      */
specifier|public
name|void
name|setBucketCount
parameter_list|(
name|int
name|bucketCount
parameter_list|)
block|{
name|this
operator|.
name|bucketCount
operator|=
name|bucketCount
expr_stmt|;
block|}
block|}
end_class

end_unit

