begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|amqp
operator|.
name|client
operator|.
name|util
package|;
end_package

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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
comment|/**  * Generator for Globally unique Strings.  */
end_comment

begin_class
specifier|public
class|class
name|IdGenerator
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
name|IdGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|UNIQUE_STUB
decl_stmt|;
specifier|private
specifier|static
name|int
name|instanceCount
decl_stmt|;
specifier|private
specifier|static
name|String
name|hostName
decl_stmt|;
specifier|private
name|String
name|seed
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|sequence
init|=
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|int
name|length
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_IDGENERATOR_PORT
init|=
literal|"activemq.idgenerator.port"
decl_stmt|;
static|static
block|{
name|String
name|stub
init|=
literal|""
decl_stmt|;
name|boolean
name|canAccessSystemProps
init|=
literal|true
decl_stmt|;
try|try
block|{
name|SecurityManager
name|sm
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|checkPropertiesAccess
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
name|canAccessSystemProps
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|canAccessSystemProps
condition|)
block|{
name|int
name|idGeneratorPort
init|=
literal|0
decl_stmt|;
name|ServerSocket
name|ss
init|=
literal|null
decl_stmt|;
try|try
block|{
name|idGeneratorPort
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_IDGENERATOR_PORT
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Using port {}"
argument_list|,
name|idGeneratorPort
argument_list|)
expr_stmt|;
name|hostName
operator|=
name|getLocalHostName
argument_list|()
expr_stmt|;
name|ss
operator|=
operator|new
name|ServerSocket
argument_list|(
name|idGeneratorPort
argument_list|)
expr_stmt|;
name|stub
operator|=
literal|"-"
operator|+
name|ss
operator|.
name|getLocalPort
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"-"
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"could not generate unique stub by using DNS and binding to local port"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not generate unique stub by using DNS and binding to local port: {} {}"
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Restore interrupted state so higher level code can deal with it.
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Closing the server socket failed"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Closing the server socket failed"
operator|+
literal|" due "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|hostName
operator|==
literal|null
condition|)
block|{
name|hostName
operator|=
literal|"localhost"
expr_stmt|;
block|}
name|hostName
operator|=
name|sanitizeHostName
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
if|if
condition|(
name|stub
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|stub
operator|=
literal|"-1-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"-"
expr_stmt|;
block|}
name|UNIQUE_STUB
operator|=
name|stub
expr_stmt|;
block|}
comment|/**      * Construct an IdGenerator      *      * @param prefix      *      The prefix value that is applied to all generated IDs.      */
specifier|public
name|IdGenerator
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
synchronized|synchronized
init|(
name|UNIQUE_STUB
init|)
block|{
name|this
operator|.
name|seed
operator|=
name|prefix
operator|+
name|UNIQUE_STUB
operator|+
operator|(
name|instanceCount
operator|++
operator|)
operator|+
literal|":"
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|this
operator|.
name|seed
operator|.
name|length
argument_list|()
operator|+
operator|(
literal|""
operator|+
name|Long
operator|.
name|MAX_VALUE
operator|)
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|IdGenerator
parameter_list|()
block|{
name|this
argument_list|(
literal|"ID:"
operator|+
name|hostName
argument_list|)
expr_stmt|;
block|}
comment|/**      * As we have to find the host name as a side-affect of generating a unique stub, we allow      * it's easy retrieval here      *      * @return the local host name      */
specifier|public
specifier|static
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
comment|/**      * Generate a unique id      *      * @return a unique id      */
specifier|public
specifier|synchronized
name|String
name|generateId
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|sanitizeHostName
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|char
name|ch
range|:
name|hostName
operator|.
name|toCharArray
argument_list|()
control|)
block|{
comment|// only include ASCII chars
if|if
condition|(
name|ch
operator|<
literal|127
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|changed
condition|)
block|{
name|String
name|newHost
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sanitized hostname from: {} to: {}"
argument_list|,
name|hostName
argument_list|,
name|newHost
argument_list|)
expr_stmt|;
return|return
name|newHost
return|;
block|}
else|else
block|{
return|return
name|hostName
return|;
block|}
block|}
comment|/**      * Generate a unique ID - that is friendly for a URL or file system      *      * @return a unique id      */
specifier|public
name|String
name|generateSanitizedId
parameter_list|()
block|{
name|String
name|result
init|=
name|generateId
argument_list|()
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|'_'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * From a generated id - return the seed (i.e. minus the count)      *      * @param id      *        the generated identifier      * @return the seed      */
specifier|public
specifier|static
name|String
name|getSeedFromId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|String
name|result
init|=
name|id
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|id
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>
literal|0
operator|&&
operator|(
name|index
operator|+
literal|1
operator|)
operator|<
name|id
operator|.
name|length
argument_list|()
condition|)
block|{
name|result
operator|=
name|id
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * From a generated id - return the generator count      *      * @param id      *      The ID that will be parsed for a sequence number.      *      * @return the sequence value parsed from the given ID.      */
specifier|public
specifier|static
name|long
name|getSequenceFromId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|long
name|result
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|id
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>
literal|0
operator|&&
operator|(
name|index
operator|+
literal|1
operator|)
operator|<
name|id
operator|.
name|length
argument_list|()
condition|)
block|{
name|String
name|numStr
init|=
name|id
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|numStr
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Does a proper compare on the Id's      *      * @param id1 the lhs of the comparison.      * @param id2 the rhs of the comparison.      *      * @return 0 if equal else a positive if {@literal id1> id2} ...      */
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|String
name|id1
parameter_list|,
name|String
name|id2
parameter_list|)
block|{
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|seed1
init|=
name|IdGenerator
operator|.
name|getSeedFromId
argument_list|(
name|id1
argument_list|)
decl_stmt|;
name|String
name|seed2
init|=
name|IdGenerator
operator|.
name|getSeedFromId
argument_list|(
name|id2
argument_list|)
decl_stmt|;
if|if
condition|(
name|seed1
operator|!=
literal|null
operator|&&
name|seed2
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|seed1
operator|.
name|compareTo
argument_list|(
name|seed2
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|long
name|count1
init|=
name|IdGenerator
operator|.
name|getSequenceFromId
argument_list|(
name|id1
argument_list|)
decl_stmt|;
name|long
name|count2
init|=
name|IdGenerator
operator|.
name|getSequenceFromId
argument_list|(
name|id2
argument_list|)
decl_stmt|;
name|result
operator|=
call|(
name|int
call|)
argument_list|(
name|count1
operator|-
name|count2
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * When using the {@link java.net.InetAddress#getHostName()} method in an      * environment where neither a proper DNS lookup nor an<tt>/etc/hosts</tt>      * entry exists for a given host, the following exception will be thrown:      *<code>      * java.net.UnknownHostException:&lt;hostname&gt;:&lt;hostname&gt;      *  at java.net.InetAddress.getLocalHost(InetAddress.java:1425)      *   ...      *</code>      * Instead of just throwing an UnknownHostException and giving up, this      * method grabs a suitable hostname from the exception and prevents the      * exception from being thrown. If a suitable hostname cannot be acquired      * from the exception, only then is the<tt>UnknownHostException</tt> thrown.      *      * @return The hostname      *      * @throws UnknownHostException if the given host cannot be looked up.      *      * @see java.net.InetAddress#getLocalHost()      * @see java.net.InetAddress#getHostName()      */
specifier|protected
specifier|static
name|String
name|getLocalHostName
parameter_list|()
throws|throws
name|UnknownHostException
block|{
try|try
block|{
return|return
operator|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|)
operator|.
name|getHostName
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|uhe
parameter_list|)
block|{
name|String
name|host
init|=
name|uhe
operator|.
name|getMessage
argument_list|()
decl_stmt|;
comment|// host = "hostname: hostname"
if|if
condition|(
name|host
operator|!=
literal|null
condition|)
block|{
name|int
name|colon
init|=
name|host
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|>
literal|0
condition|)
block|{
return|return
name|host
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
return|;
block|}
block|}
throw|throw
name|uhe
throw|;
block|}
block|}
block|}
end_class

end_unit

