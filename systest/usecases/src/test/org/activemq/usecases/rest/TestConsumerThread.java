begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_class
class|class
name|TestConsumerThread
extends|extends
name|Thread
block|{
specifier|private
name|RESTLoadTest
name|c
decl_stmt|;
specifier|public
name|int
name|success
decl_stmt|;
specifier|private
name|int
name|messagecount
decl_stmt|;
name|TestConsumerThread
parameter_list|(
name|RESTLoadTest
name|p
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|c
operator|=
name|p
expr_stmt|;
name|success
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|counter
operator|++
expr_stmt|;
name|messagecount
operator|=
name|count
expr_stmt|;
block|}
specifier|private
name|int
name|performGet
parameter_list|(
name|String
name|urlString
parameter_list|)
block|{
try|try
block|{
name|URL
name|url
decl_stmt|;
name|HttpURLConnection
name|urlConn
decl_stmt|;
name|DataOutputStream
name|printout
decl_stmt|;
name|DataInputStream
name|input
decl_stmt|;
comment|// URL of CGI-Bin script.
name|url
operator|=
operator|new
name|URL
argument_list|(
name|urlString
argument_list|)
expr_stmt|;
comment|// URL connection channel.
name|urlConn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|urlConn
operator|.
name|setDoInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|urlConn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|urlConn
operator|.
name|setUseCaches
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Get response data.
name|input
operator|=
operator|new
name|DataInputStream
argument_list|(
name|urlConn
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|str
decl_stmt|;
while|while
condition|(
literal|null
operator|!=
operator|(
operator|(
name|str
operator|=
name|input
operator|.
name|readLine
argument_list|()
operator|)
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CONSUME:"
operator|+
name|str
argument_list|)
expr_stmt|;
block|}
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|success
operator|++
expr_stmt|;
return|return
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|me
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"MalformedURLException: "
operator|+
name|me
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"IOException: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messagecount
condition|;
name|i
operator|++
control|)
block|{
name|int
name|e
init|=
name|performGet
argument_list|(
literal|"http://127.0.0.1:8080/jms/FOO/BAR?id=1234&readTimeout=60000"
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|1
condition|)
block|{
break|break;
block|}
block|}
name|c
operator|.
name|counter
operator|--
expr_stmt|;
block|}
block|}
end_class

end_unit

