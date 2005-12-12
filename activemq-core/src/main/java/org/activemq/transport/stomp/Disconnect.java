begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (c) 2005 Your Corporation. All Rights Reserved.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ShutdownInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
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
name|util
operator|.
name|Properties
import|;
end_import

begin_class
class|class
name|Disconnect
implements|implements
name|StompCommand
block|{
specifier|public
name|CommandEnvelope
name|build
parameter_list|(
name|String
name|line
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|in
operator|.
name|readByte
argument_list|()
operator|!=
literal|0
condition|)
block|{         }
return|return
operator|new
name|CommandEnvelope
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

